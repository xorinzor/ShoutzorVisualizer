package com.jorinvermeulen.shoutzor.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
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

	private 	BufferedImage buffer;
	private		Graphics2D g;
	private		Image image;
	private 	Texture2D boardTexture;
	
	private int canvasWidth;
	private int canvasHeight;
	
	private 	Material material;
	private		ShoutzorVisualizer visualizer;
	
	public NowPlaying(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer, x, y, z);
		
		this.visualizer = visualizer;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.canvasWidth = 600;
		this.canvasHeight = 240;

		buffer = new BufferedImage(this.canvasWidth,this.canvasHeight, BufferedImage.TYPE_4BYTE_ABGR);
		g = buffer.createGraphics();
		g.setFont(new Font("RobotoCondensed", Font.BOLD, 35));

		image = new AWTLoader().load(buffer, true);   
		boardTexture = new Texture2D(image);

		Quad quad = new Quad(30, 12);

		Spatial textCube = new Geometry("box", quad);
		
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setTexture("ColorMap", boardTexture);
		material.setTransparent(true);
		material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		
		textCube.setQueueBucket(Bucket.Transparent);
		textCube.setMaterial(material);
		textCube.rotate(0f, -1.558f, 0f);
		textCube.setLocalTranslation(this.x, this.y, this.z);
		
		rootNode.attachChild(textCube);
	}
	
	private int vertScrollPosX = 10;
	private int vertMovSpeedX = 1;
	private int marginLeft = 20;
	private int marginRight = 50;
	private String latestNowPlaying = "";
	
	private int timeoutCounter = 0;
	
	@Override
	public void draw(float tpf) {
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, 600, 240);
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.BLACK);
		g.drawString("Now Playing:",0,35);
		g.setColor(Color.ORANGE);
		
		if(latestNowPlaying != visualizer.getNowPlaying()) {
			latestNowPlaying = visualizer.getNowPlaying();
			vertScrollPosX = marginLeft;
			timeoutCounter = 0;
		}
		
		if(timeoutCounter > 100) {
			int titleWidth = g.getFontMetrics().stringWidth(visualizer.getNowPlaying());
			if(titleWidth > this.canvasWidth) {
				vertScrollPosX = vertScrollPosX - vertMovSpeedX;
				int endPos = vertScrollPosX + titleWidth;
				
				if(endPos < canvasWidth - marginRight) {
					vertScrollPosX = marginLeft;
					timeoutCounter = 0;
				}
			} else {
				vertScrollPosX = marginLeft;
				timeoutCounter = 0;
			}
		}
		
		timeoutCounter++;
		
		g.drawString(visualizer.getNowPlaying(), vertScrollPosX, 80);
		
		//g.setColor(Color.BLACK);
		//g.drawString("Requested by:",0,150);
		//g.setColor(Color.ORANGE);
		//g.drawString("Xorinzor",0,200);
		
		image = new AWTLoader().load(buffer, true);
		boardTexture.setImage(image);
	}

}
