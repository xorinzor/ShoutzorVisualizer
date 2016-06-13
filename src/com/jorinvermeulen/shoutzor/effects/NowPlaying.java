package com.jorinvermeulen.shoutzor.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
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

		  BufferedImage buffer = new BufferedImage(600,240, BufferedImage.TYPE_4BYTE_ABGR);
		   
		  Graphics2D g = buffer.createGraphics();
		  g.setFont(new Font("RobotoCondensed", Font.BOLD, 35));
		  g.setComposite(AlphaComposite.Clear);
		  g.fillRect(0, 0, 99, 99);
		  g.setComposite(AlphaComposite.Src);
		  g.setColor(Color.WHITE);
		  g.drawString("Now Playing:",0,35);
		  g.setColor(Color.ORANGE);
		  g.drawString("Darude - Sandstorm",0,70);
		  g.dispose();
		
		  Image image = new AWTLoader().load(buffer, true);
		       
		  Texture2D boardTexture = new Texture2D(image);

		  Quad quad = new Quad(30, 12);
		  
		  Spatial textCube = new Geometry("box", quad);
		  Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		  material.setTexture("ColorMap", boardTexture);
		  material.setTransparent(true);
		  material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		  textCube.setQueueBucket(Bucket.Transparent);
		  textCube.setMaterial(material);
		  textCube.rotate(0f, -1.558f, 0f);
		  textCube.setLocalTranslation(x, y, z);
		  rootNode.attachChild(textCube);
	}
	
	@Override
	public void draw(float tpf) {
	}

}
