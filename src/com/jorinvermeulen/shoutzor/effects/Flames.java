package com.jorinvermeulen.shoutzor.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.material.MatParamOverride;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import com.jorinvermeulen.shoutzor.main.ShoutzorVisualizer;

import ddf.minim.analysis.FFT;

public class Flames extends Effect {
	
	private float x;
	private float y;
	private float z;
	
	private float[][] newState;
	private float[][] currentState;
	private float[] fuel;
    
	private int rowCount;
	private int columnCount;
	private float decay;
	
	private Node container;
	private List<Spatial> flameCubes;
	
	private Random rand = new Random();
	private FFT fft;
	
	public Flames(ShoutzorVisualizer visualizer) {
		this(visualizer, 0f, 0f, 0f);
	}
	
	public Flames(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer, x, y, z);
		
		flameCubes = new ArrayList<Spatial>();
		container = new Node();
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.fft = visualizer.getFFT();
		
		float width = 0.20f;
		float height = 0.20f;
		
		float spacingX = 0.05f;
		float spacingY = 0.05f;
		
		decay = 0.90f;
		rowCount = 20;
		columnCount = this.fft.specSize();
		 
		newState 		= new float[rowCount][columnCount];
		currentState 	= new float[rowCount][columnCount];
		fuel			= new float[columnCount];
		
		for(int i = 0; i < columnCount; i++) {
			fuel[i] = this.fft.getBand(i);
		}
		
		Box b = new Box(width, height, 0.1f);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Red);   // set color of material to blue
		Spatial cube;
		
		int incrementX = 0;
		int incrementY = 0;
		int item = 0;
		for(float[] a : currentState) {
			for(float col : a) {
				flameCubes.add(new Geometry("Box", b));
	        	cube = flameCubes.get(item);
	    		cube.setMaterial(mat);
	    		cube.setLocalTranslation(
		    				(float) (0f + ((width + spacingX) * incrementX)), 
		    				(float) (0f + ((height + spacingY) * incrementY)), 
		    				0f
	    				);
	    		container.attachChild(cube);
	    		item++;
	    		incrementX++;
			}
			incrementX = 0;
			incrementY++;
		}
		
		container.setLocalTranslation(this.x, this.y, this.z);
		container.rotate(0f, 0f, 3.1f);
		rootNode.attachChild(container);
	}
	
	int drawCount = 0;

	public void draw(float tpf) {
		drawCount++;
		
		//Slow down the animation speed (we dont need ~90 updates per second...)
		if(drawCount < 4) {
			return;
		} else {
			drawCount = 0;
		}
		
		int rowIndex = 0;
		int columnIndex = 0;

		for(float[] rows : currentState)
		{
			for(float column : rows)
			{				
				float avg, el1, el2, el3;
				
				if(rowIndex > 0)
				{
					el1 = (columnIndex < 1) ? 0 : currentState[rowIndex-1][columnIndex-1];
					el2 = currentState[rowIndex-1][columnIndex];
					el3 = (columnIndex > rowCount - 2) ? 0 : currentState[rowIndex-1][columnIndex+1];
				}
				else 
				{
					el1 = (columnIndex < 1) ? 0 : fuel[columnIndex-1];
					el2 = fuel[columnIndex];
					el3 = (columnIndex > rowCount - 2) ? 0 : fuel[columnIndex+1];
				}
				
				//Get the average from the cubes below and add a random value for variety
				avg = (el1 + el2 + el3) / 3;
				avg = avg * ((decay - (float) (rowIndex * 0.01))- ((float) (rand.nextDouble() * 0.1)));
						
				newState[rowIndex][columnIndex] = avg;
				
				columnIndex++;
			}
			
			columnIndex = 0;
			rowIndex++;
		}
		
		int row = 0;
		int column = 0;
		for(Spatial c : flameCubes)
		{
			c.clearMatParamOverrides();
			c.addMatParamOverride(new MatParamOverride(VarType.Vector4, "Color", new ColorRGBA(newState[row][column], 0f, 0f, 1f)));
			column++;
			if(column == columnCount) {
				column = 0;
				row++;
			}
		}
		
		currentState = newState;

		for(int i = 0; i < columnCount; i++) {
			fuel[i] = this.fft.getBand(i);
		}
		
	}
}