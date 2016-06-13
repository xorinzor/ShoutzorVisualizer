package com.jorinvermeulen.shoutzor.main;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jorinvermeulen.shoutzor.effects.AudioBars;
import com.jorinvermeulen.shoutzor.effects.Effect;
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
	
	public static void main(String[] args)
	{
		ShoutzorVisualizer app = new ShoutzorVisualizer();
        app.start(); // start the game
	}
	
	@Override
	public void simpleInitApp() {
		//initialize variables
		effectList = new ArrayList<Effect>();
		
		//Set our camera at a fixed position, disable all keybindings
		inputManager.clearMappings();
		flyCam.setEnabled(false);
		
		//cam.setLocation(new Vector3f(0, 5, 50));
		Quaternion rotation = cam.getRotation().fromAngles(0.29f, 2.355f, 0f);
		cam.setRotation(rotation);
		cam.setLocation(new Vector3f(-30f, 24f, 30f));
		
		//Add the assets location
		String userHome = System.getProperty("user.dir") + "\\assets\\";
		assetManager.registerLocator(userHome, FileLocator.class);
		
		//Load music analysis
		minim = new Minim(new MinimInput());
		
		int size = 1024;
		
		//song = minim.loadFile("http://jorinvermeulen.com/downloads/song.mp3", size);
		song = minim.loadFile("http://relay4.slayradio.org:8000/", size);
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
        addEffect(new Logo(this, 32f, 20f, -5f));
        addEffect(new NowPlaying(this, 23f, 2f, -15f));
        //addEffect(new Flames(this, 50f, 0f, -20f));
        
        this.setDisplayFps(true);
        this.setDisplayStatView(false);
        
        rootNode.detachChildNamed("fpsText");
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
	
	@Override
	public void simpleUpdate(float tpf) {
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
