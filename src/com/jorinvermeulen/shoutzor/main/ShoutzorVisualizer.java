package com.jorinvermeulen.shoutzor.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.system.AppSettings;
import com.jorinvermeulen.shoutzor.effects.AudioBars;
import com.jorinvermeulen.shoutzor.effects.Effect;
import com.jorinvermeulen.shoutzor.effects.Flames;
import com.jorinvermeulen.shoutzor.effects.Decoration;
import com.jorinvermeulen.shoutzor.effects.Logo;
import com.jorinvermeulen.shoutzor.effects.NowPlaying;
import com.jorinvermeulen.shoutzor.processing.MinimInput;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
	
public class ShoutzorVisualizer extends SimpleApplication {

	private List<Effect> effectList;
	private Minim minim;
	private AudioPlayer song;
	private BeatDetect beat;
	private FFT fft;
	
	private String nowPlaying = "";
	
	private String serverUrl = "http://192.168.178.117:8000/shoutzor";
	private String shoutzorUrl = "http://192.168.178.117";
	
	private JsonParser jp;
	private URL url;
	
	public static void main(String[] args)
	{
		ShoutzorVisualizer app = new ShoutzorVisualizer();
		
		AppSettings settings = new AppSettings(true);
		settings.setFrameRate(60);
		settings.setTitle("Shoutzor Visualizer");
		settings.setMinHeight(1050);
		settings.setMinWidth(1680);
		app.setSettings(settings);
		
        app.start(); // start the game
	}
	
	@Override
	public void simpleInitApp() {
		
		String baseIP = "";
		
		try {
			baseIP = Utility.readFile("config.txt", StandardCharsets.UTF_8);
		} catch (IOException e1) {
			System.exit(-1);
		}
		
		this.serverUrl = "http://" + baseIP + ":8000/shoutzor";
		this.shoutzorUrl = "http://" + baseIP;
		
		String sURL = this.shoutzorUrl + "/shoutzorapi?method=nowplaying"; //just a string
		
		try {
			url = new URL(sURL);
		} catch (Exception e) {
			System.exit(-1);
		}
		
		jp = new JsonParser(); //from gson
		
		//initialize variables
		effectList = new ArrayList<Effect>();
		
		//Set our camera at a fixed position, disable all keybindings
		inputManager.clearMappings();
		flyCam.setEnabled(false);
		
		//cam.setLocation(new Vector3f(0, 5, 50));
		Quaternion rotation = cam.getRotation().fromAngles(0.29f, 2.355f, 0f);
		cam.setRotation(rotation);
		cam.setLocation(new Vector3f(-30f, 24f, 30f));
		
		//Load music analysis
		minim = new Minim(new MinimInput());
		
		int size = 1024;
		
		//song = minim.loadFile("http://jorinvermeulen.com/downloads/song.mp3", size);
		song = minim.loadFile(this.serverUrl, size);
		song.play();
		
		//beat = new BeatDetect(song.bufferSize(), song.sampleRate());
		beat = new BeatDetect();
		beat.setSensitivity(200);
		fft = new FFT(song.bufferSize(), song.bufferSize());
		fft.logAverages(30, 5);
		
		//Add Post-Processing effects
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom 		= new BloomFilter(BloomFilter.GlowMode.Scene);
        
        //Set Bloom parameters
        bloom.setBloomIntensity(1.4f);
        bloom.setDownSamplingFactor(1f);
        bloom.setExposurePower(3f);
        
        //Add bloom to the Post-Processing effects
        fpp.addFilter(bloom);
        
        //Add the effects to our viewport
        viewPort.addProcessor(fpp);
        
        //Add the scene elements
        addEffect(new AudioBars(this, -15f, 1f, -24.9f));
        addEffect(new Decoration(this));
        addEffect(new Logo(this, 32f, 20f, -4f));
        addEffect(new Flames(this, 22f, 25.5f, -4f));
        addEffect(new NowPlaying(this, 23f, 0f, -15f));
        
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        
        rootNode.detachChildNamed("fpsText");
        
        this.getStreamMetaData();
	}
	
	public void getStreamMetaData() {
		// Connect to the URL using java's native library
		try {
			HttpURLConnection request = (HttpURLConnection) this.url.openConnection();
			request.connect();
			
			// Convert to a JSON object to print data
			JsonElement root = this.jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
			JsonObject rootobj = root.getAsJsonObject().get("data").getAsJsonObject(); //May be an array, may be an object.
			
			String artistNowPlaying = "";
			
			if(rootobj.get("artist") != null) {
				JsonArray artists = rootobj.get("artist").getAsJsonArray();
				JsonObject artist;
				for(int i = 0; i < artists.size(); i++)
				{
					artist = artists.get(i).getAsJsonObject();
					
					if(artistNowPlaying != "") {
						artistNowPlaying += ", ";
					}
					
					artistNowPlaying += artist.get("name").getAsString();
				}
			}
			
			if(artistNowPlaying == "") {
				artistNowPlaying = "Unknown";
			}
			
			String titleNowPlaying = "";
			
			if(rootobj.get("title") != null) {
				titleNowPlaying = rootobj.get("title").getAsString();
				
			}
			
			if(titleNowPlaying == "") {
				titleNowPlaying = "Untitled";
			}
			
			this.nowPlaying = titleNowPlaying + " - " + artistNowPlaying;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getNowPlaying() {
		return this.nowPlaying;
	}
	
	public void addEffect(Effect effect) {
		effectList.add(effect);
	}
	
	public BeatDetect getBeatDetect() {
		return this.beat;
	}
	
	public FFT getFFT() {
		return this.fft;
	}
	
	private int metaDataUpdateCount = 0;
	
	@Override
	public void simpleUpdate(float tpf) {
		
		metaDataUpdateCount++;
		
		if(metaDataUpdateCount > 700) {
			this.getStreamMetaData();
			metaDataUpdateCount = 0;
		}
		
		fft.forward(song.mix);
		beat.detect(song.mix);
		
		for(Effect e : this.effectList) {
			e.draw(tpf);
		}
	}
	
	@Override
	public void destroy() {
		song.close();
		minim.stop();
	}
}
