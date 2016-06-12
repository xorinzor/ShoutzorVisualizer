package com.jorinvermeulen.shoutzor.effects;

import com.jme3.light.DirectionalLight;
import com.jme3.material.MatParamOverride;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jorinvermeulen.shoutzor.main.ShoutzorVisualizer;

import ddf.minim.analysis.FFT;

public class Logo extends Effect {
	
	protected 	Spatial scnLogo;
	
	private		float x;
	private		float y;
	private		float z;
	
	private		float decay;
	private 	float scale;

	private 	FFT fft;
	private 	DirectionalLight sun = new DirectionalLight();
	
	public Logo(ShoutzorVisualizer visualizer) {
		this(visualizer, 0f, 0f, 0f);
	}
	
	public Logo(ShoutzorVisualizer visualizer, float x, float y, float z) {
		super(visualizer);
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.decay = 0.97f;
		
		this.fft = visualizer.getFFT();
		
		//SCN Logo
        scnLogo = assetManager.loadModel("Models/SCNLogo.mesh.xml");
		
        //Set the material
        Material mat = new Material(assetManager,"Materials/Lighting.j3md");  // create a simple material
        mat.setColor("Ambient", new ColorRGBA(252f / 255f, 19f / 255f, 104f / 255f, 1f));  // Color of the material
        mat.setColor("Diffuse", new ColorRGBA(252f / 255f, 19f / 255f, 104f / 255f, 1f));  // Reflected light color
        
        //Modify the positioning of the logo
        scnLogo.rotate(1.57f, -1.558f, 0f);
        scnLogo.setLocalTranslation(this.x, this.y, this.z);
        scnLogo.setMaterial(mat);
        
        // Sun Light
        sun.setDirection(new Vector3f(30,10,30).normalize());
        sun.setColor(new ColorRGBA(252f / 255f, 19f / 255f, 104f / 255f, 1f));
        rootNode.addLight(sun);
        
        //Add the logo to the scene
        rootNode.attachChild(scnLogo);
	}
	
	public void draw(float tpf) {
		float total = 0f;
		for(int i = 0; i < 4; i++) {
			total += this.fft.getBand(i);
		}
		
		total = total / 4f;
		boolean kick = total > 85f;
		
		float baseScale = 10f;
		
		if(kick) {
			scale = baseScale + 1.5f;
			
			ColorRGBA r1 = ColorRGBA.randomColor();
			
			scnLogo.clearMatParamOverrides();
			scnLogo.addMatParamOverride(new MatParamOverride(VarType.Vector4, "Ambient", r1));
			scnLogo.addMatParamOverride(new MatParamOverride(VarType.Vector4, "Diffuse", r1));
			sun.setColor(r1);
		}
		
		scnLogo.setLocalScale(scale);
		scnLogo.setLocalTranslation(this.x-(scale/2), this.y-(scale/2), this.z);
		
		scale = scale * decay;
		
		if(scale < baseScale) {
			scale = baseScale;
		}
	}

}