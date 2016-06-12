package com.jorinvermeulen.shoutzor.effects;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jorinvermeulen.shoutzor.main.ShoutzorVisualizer;

public abstract class Effect {
	
	protected Node rootNode;
	protected AssetManager assetManager;
	
	public Effect(ShoutzorVisualizer visualizer) {
		this(visualizer, 0f, 0f, 0f);
	}
	
	public Effect(ShoutzorVisualizer visualizer, float x, float y, float z) {
		this.rootNode = visualizer.getRootNode();
		this.assetManager = visualizer.getAssetManager();
	}
	
	public void draw(float tpf) {
		return;
	}
}