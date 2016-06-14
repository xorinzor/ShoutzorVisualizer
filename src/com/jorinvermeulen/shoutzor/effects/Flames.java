package com.jorinvermeulen.shoutzor.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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

public class Flames extends Effect {
	
	private float x;
	private float y;
	private float z;
	
	private float[] fuel;
    private Color[][] colorState;
	
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
	
	private FFT fft;
	
	private Spatial flamesRight;
	
	public Flames(ShoutzorVisualizer visualizer) {
		this(visualizer, 0f, 0f, 0f);
	}
	
	public Flames(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer, x, y, z);
		
		container = new Node();
		
		this.fft = visualizer.getFFT();
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.divider = 32;
		
		this.canvasWidth = 100;
		this.canvasHeight = 150;
		this.width = 5;
		this.height = 3;
		this.spacingX = 3;
		this.spacingY = 1;
		
		decay = 0.88f;
		rowCount = 30;
		columnCount = this.fft.specSize() / this.divider;
		
		fuel			= new float[columnCount];
		colorState		= new Color[rowCount][columnCount];
		
		//Create a canvas to draw on
		buffer = new BufferedImage(this.canvasWidth, this.canvasHeight, BufferedImage.TYPE_4BYTE_ABGR);
		g = buffer.createGraphics();
		
		//Create a texture from the canvas image
		image = new AWTLoader().load(buffer, true);   
		boardTexture = new Texture2D(image);
		
		imageFlipped = new AWTLoader().load(buffer, false);   
		boardTextureFlipped = new Texture2D(imageFlipped);

		//Create the Quad to render
		Quad quad = new Quad(11, 13);

		Spatial flamesLeft = new Geometry("box", quad);
		flamesRight = new Geometry("box", quad);
		
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setTexture("ColorMap", boardTexture);
		material.setTransparent(true);
		material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		
		flamesLeft.setQueueBucket(Bucket.Transparent);
		flamesLeft.setMaterial(material);
		flamesLeft.rotate(0f, -1.558f, -1.558f);
		flamesLeft.setLocalTranslation(this.x, this.y, this.z - 13f);
		
		flamesRight.setQueueBucket(Bucket.Transparent);
		flamesRight.setMaterial(material);
		flamesRight.rotate(0f, -1.558f, -1.558f);
		flamesRight.setLocalTranslation(this.x, this.y, this.z + 13f);
		flamesRight.addMatParamOverride(new MatParamOverride(VarType.Vector4, "ColorMap", boardTextureFlipped));
		
		container.attachChild(flamesLeft);
		container.attachChild(flamesRight);
		
		rootNode.attachChild(container);
		
		this.prepareGradientSteps();
	}
	
	private List<Color> gradientSteps;
	
	private void prepareGradientSteps() {
		gradientSteps = new ArrayList<Color>();
		
		Color color1 = Color.BLUE;
        Color color2 = Color.RED;
        
        int steps = 80;

        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / (float) steps;
            
            int red = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
            int green = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
            int blue = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));

            gradientSteps.add(new Color(red, green, blue));
        }
	}
	
	private Color getGradientColor(int value) {
		if(value < gradientSteps.size()) {
			return gradientSteps.get(value);
		} else {
			return gradientSteps.get(gradientSteps.size()-1);
		}
	}
	
	private int drawCount = 0;
	
	@SuppressWarnings("unused")
	public void drawFlames() {
		Color color = new Color(1f, 1f, 1f, 0f);
		float size = 0f;
		int k = 0;
		
		//Provide fuel to burn, by using the FFT Spectrum
		for(int i = 0; i < columnCount; i++) {
			size = 0f;
			
			for(int j = 0; j < this.divider; j++) {
				size += this.fft.getBand(k + j);
			}
			
			size = size / this.divider;
			
			if(fuel[i] > 0)
			{
				fuel[i] = (size + fuel[i]);
			} 
			else 
			{
				fuel[i] = size;
			}
			
			k += this.divider;
		}
		
		drawCount++;
		
		if(drawCount < 4) {
			return;
		}
		
		drawCount = 0;
		
		//Fill the canvas with a transparent background
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
		
		//Start drawing on the transparent background
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.ORANGE);
		
		int rowIndex = 0;
		int columnIndex = 0;

		//Foreach row
		for(Color[] rows : colorState)
		{
			//Foreach column
			for(Color column : rows)
			{				
				float el, elTemp = 0;
				
				//Check if we need to use fuel or not
				//Dont use fuel
				if(rowIndex > 0)
				{
					color = colorState[rowIndex-1][columnIndex];
					color = new Color(color.getRed()/255f, color.getBlue()/255f, color.getGreen()/255f, (color.getAlpha()/255f) * decay);
					colorState[rowIndex][columnIndex] = color;
				}
				//Burn fuel
				else 
				{
					el = fuel[columnIndex];
					
					elTemp = (el > 1f) ? 1f : el;	
					
					color = this.getGradientColor((int) Math.round(el) * 10);
					
					colorState[rowIndex][columnIndex] = color;
				}

				g.setColor(color);
				g.fillRect(
						columnIndex * this.width + columnIndex * this.spacingX,
						rowIndex * this.height + rowIndex * this.spacingY, 
						this.width, 
						this.height);
				
				columnIndex++;
			}
			
			columnIndex = 0;
			rowIndex++;
		}
		
		image = new AWTLoader().load(buffer, true);
		boardTexture.setImage(image);
		
		imageFlipped = new AWTLoader().load(buffer, true);
		boardTextureFlipped = new Texture2D(imageFlipped);
		flamesRight.clearMatParamOverrides();
		flamesRight.addMatParamOverride(new MatParamOverride(VarType.Texture2D, "ColorMap", boardTextureFlipped));
		
		//Reset fuel
		k = 0;
		for(int i = 0; i < columnCount; i++) {
			fuel[i] = 0f;
			k += this.divider;
		}
	}

	public void draw(float tpf) {
		this.drawFlames();
	}
}