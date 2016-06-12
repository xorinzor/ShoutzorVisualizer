package com.jorinvermeulen.shoutzor.effects;

import java.util.ArrayList;
import java.util.List;

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

public class AudioBars extends Effect {

	protected 	List<Spatial> audioBars;
	
	private		float x;
	private		float y;
	private		float z;
	private 	float width;
	private 	float spacing;
	private 	FFT fft;
	
	private 	int divider;
	
	private 	Node audioBarScene;
	
	public AudioBars(ShoutzorVisualizer visualizer) {
		this(visualizer, 0f, 0f, 0f);
	}
	
	public AudioBars(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer, x, y, z);
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.divider = 64;
		
		this.width = 1f;
		this.spacing = 3f;
		this.fft = visualizer.getFFT();
		
		Box 		b = new Box(this.width, 1f, 0.1f);
        Material 	mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Green);   // set color of material to blue
        
        this.audioBars = new ArrayList<Spatial>();
        Spatial cube;
        
        audioBarScene = new Node();
        
        for(int i = 0; i < (this.fft.specSize() / this.divider); i++) {
        	audioBars.add(new Geometry("Box", b));
        	cube = audioBars.get(i);
    		cube.setMaterial(mat);                   // set the cube's material
    		cube.setLocalTranslation(i * this.width + i* this.spacing + this.x, this.y, this.z);
    		audioBarScene.attachChild(cube);
        }
        
        rootNode.attachChild(audioBarScene);
	}

	@Override
	public void draw(float tpf) {
		//Update the audio bars
		int j = 0;
		int k = 0;
		
		for(Spatial c : this.audioBars) {
			float size = 0f;
			
			for(int i = 0; i < this.divider; i++) {
				size += (float) fft.getBand(k + i);
			}
			
			size = (float) size / (float) this.divider;
			
			c.setLocalScale(1, size, 1);
			c.setLocalTranslation(j * this.width + j * this.spacing + this.x, this.y  + size + 0.1f, this.z);
			
			k = k + this.divider;
			j++;
		}
		
		//Apply color effects
		float total = 0f;
		for(int i = 0; i < 7; i++) {
			total += this.fft.getBand(i);
		}
		
		total = total / 7f;
		boolean kick = total > 85f;
		
		if(kick) {
			audioBarScene.clearMatParamOverrides();
			
			ColorRGBA r1 = ColorRGBA.randomColor();
			
			audioBarScene.addMatParamOverride(new MatParamOverride(VarType.Vector4, "Color", r1));
		}
	}

}