package com.jorinvermeulen.shoutzor.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jorinvermeulen.shoutzor.main.ShoutzorVisualizer;

public class NowPlaying extends Effect {
	
	private		float x;
	private		float y;
	private		float z;

	public NowPlaying(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer, x, y, z);
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		BitmapFont font = assetManager.loadFont("Fonts/font.fnt"); 
		
		BitmapText text = new BitmapText(font, false);
		text.setQueueBucket(Bucket.Transparent);
		text.setColor(ColorRGBA.Orange);
		text.setSize(4f);
		text.setText("Now Playing:");
		text.setBox(new Rectangle(-text.getLineWidth(), -text.getLineHeight(), text.getLineWidth(), text.getLineHeight()));
		text.setLocalTranslation(this.x, this.y, this.z);
		text.rotate(0f, -1.558f, 0f);
		
		visualizer.getRootNode().attachChild(text);

		  BufferedImage buffer = new BufferedImage(100,100, BufferedImage.TYPE_4BYTE_ABGR);
		   
		  Graphics2D g = buffer.createGraphics();
		  g.setComposite(AlphaComposite.Clear);
		  g.fillRect(0, 0, 99, 99);
		  g.setComposite(AlphaComposite.Src);
		  g.setColor(Color.WHITE);
		  g.drawString("Hello World!",10,10);
		  g.dispose();
		
		  Image image = new AWTLoader().load(buffer, true);
		       
		  Texture2D boardTexture = new Texture2D(image);

		  Quad quad = new Quad(10, 5);
		  
		  Spatial textCube = new Geometry("box", quad);
		  Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		  material.setTexture("ColorMap", boardTexture);
		  material.setColor("Color", ColorRGBA.Red);
		  material.setTransparent(true);
		  textCube.setQueueBucket(Bucket.Transparent);
		  textCube.setMaterial(material);
		  textCube.rotate(0f, 0f, 0f);
		  rootNode.attachChild(textCube);
	}
	
	@Override
	public void draw(float tpf) {
	}

}
