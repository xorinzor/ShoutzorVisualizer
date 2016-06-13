package com.jorinvermeulen.shoutzor.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.jme3.material.MatParamOverride;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.shader.VarType;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jorinvermeulen.shoutzor.main.ShoutzorVisualizer;

import ddf.minim.analysis.FFT;

public class Flames_Realistic extends Effect {
	
	private float x;
	private float y;
	private float z;
	
	private float[][] newState;
	private float[][] currentState;
	private float[] fuel;
    
	private int rowCount;
	private int columnCount;
	private float decay;
	
	private int canvasWidth;
	private int canvasHeight;
	private int width;
	private int height;
	private int spacingX;
	private int spacingY;
	
	private Node container;
	
	private int divider;
	
	private BufferedImage buffer;
	private	Graphics2D g;
	
	private	Image image;
	private Texture2D boardTexture;
	
	private	Image imageFlipped;
	private Texture2D boardTextureFlipped;
	
	private Material material;
	private Material materialMirrored;
	
	private Random rand = new Random();
	private FFT fft;
	
	private Spatial flamesRight;
	
	public Flames_Realistic(ShoutzorVisualizer visualizer) {
		this(visualizer, 0f, 0f, 0f);
	}
	
	public Flames_Realistic(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer, x, y, z);
		
		container = new Node();
		
		this.fft = visualizer.getFFT();
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.divider = 32;
		
		this.canvasWidth = 100;
		this.canvasHeight = 100;
		this.width = 5;
		this.height = 5;
		this.spacingX = 0;
		this.spacingY = 0;
		
		decay = 0.90f;
		rowCount = 20;
		columnCount = this.fft.specSize() / this.divider;
		
		System.out.println(columnCount);
		 
		newState 		= new float[rowCount][columnCount];
		currentState 	= new float[rowCount][columnCount];
		fuel			= new float[columnCount];
		
		//Create a canvas to draw on
		buffer = new BufferedImage(this.canvasWidth, this.canvasHeight, BufferedImage.TYPE_4BYTE_ABGR);
		g = buffer.createGraphics();
		
		//Create a texture from the canvas image
		image = new AWTLoader().load(buffer, true);   
		boardTexture = new Texture2D(image);
		
		imageFlipped = new AWTLoader().load(buffer, false);   
		boardTextureFlipped = new Texture2D(imageFlipped);

		//Create the Quad to render
		Quad quad = new Quad(8, 8);

		Spatial flamesLeft = new Geometry("box", quad);
		flamesRight = new Geometry("box", quad);
		
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setTexture("ColorMap", boardTexture);
		material.setTransparent(true);
		material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		
		flamesLeft.setQueueBucket(Bucket.Transparent);
		flamesLeft.setMaterial(material);
		flamesLeft.rotate(0f, -1.558f, -1.558f);
		flamesLeft.setLocalTranslation(this.x, this.y, this.z - 10f);
		
		flamesRight.setQueueBucket(Bucket.Transparent);
		flamesRight.setMaterial(material);
		flamesRight.rotate(0f, -1.558f, -1.558f);
		flamesRight.setLocalTranslation(this.x, this.y, this.z + 10f);
		flamesRight.addMatParamOverride(new MatParamOverride(VarType.Vector4, "ColorMap", boardTextureFlipped));
		
		container.attachChild(flamesLeft);
		container.attachChild(flamesRight);
		
		rootNode.attachChild(container);
	}
	
	public void drawFlames() {
		//Fill the canvas with a transparent background
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
		
		//Start drawing on the transparent background
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.ORANGE);
		
		float size = 0f;
		int k = 0;
		
		//Provide fuel to burn, by using the FFT Spectrum
		for(int i = 0; i < columnCount; i++) {
			size = 0f;
			
			for(int j = 0; j < this.divider; j++) {
				size += this.fft.getBand(k + j);
			}
			
			fuel[i] = size / this.divider;
			
			k += this.divider;
		}
		
		int rowIndex = 0;
		int columnIndex = 0;

		//Foreach row
		for(float[] rows : currentState)
		{
			//Foreach column
			for(float column : rows)
			{				
				float avg, el1, el2, el3;
				
				//Check if we need to use fuel or not
				//Dont use fuel
				if(rowIndex > 0)
				{
					el1 = (columnIndex < 1) ? 0 : currentState[rowIndex-1][columnIndex-1];
					el2 = currentState[rowIndex-1][columnIndex];
					el3 = (columnIndex+1 >= this.columnCount) ? 0 : currentState[rowIndex-1][columnIndex+1];
				}
				//Burn fuel
				else 
				{
					System.out.println(columnIndex);
					el1 = (columnIndex < 1) ? 0 : fuel[columnIndex-1];
					el2 = fuel[columnIndex];
					el3 = (columnIndex+1 >= this.columnCount) ? 0 : fuel[columnIndex+1];
				}
				
				//Get the average from the cubes below and add a random value for variety
				avg = (el1 + el2 + el3) / 3;
				avg = avg * ((decay - (float) (rowIndex * 0.01))- ((float) (rand.nextDouble() * 0.1)));
						
				if(avg > 1f) avg = 1f;
				
				newState[rowIndex][columnIndex] = avg;

				g.setColor(new Color(1, 0, 0, avg));
				g.fillRect(
						columnIndex * this.width + this.spacingX,
						rowIndex * this.height + this.spacingY, 
						this.width, 
						this.height);
				
				columnIndex++;
			}
			
			columnIndex = 0;
			rowIndex++;
		}
		
		currentState = newState;
		
		image = new AWTLoader().load(buffer, true);
		boardTexture.setImage(image);
		
		imageFlipped = new AWTLoader().load(buffer, true);
		boardTextureFlipped = new Texture2D(imageFlipped);
		flamesRight.clearMatParamOverrides();
		flamesRight.addMatParamOverride(new MatParamOverride(VarType.Texture2D, "ColorMap", boardTextureFlipped));
	}

	public void draw(float tpf) {
		this.drawFlames();
	}
}