package com.jorinvermeulen.shoutzor.effects;

import java.util.List;

import com.jme3.material.MatParamOverride;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import com.jorinvermeulen.shoutzor.main.ShoutzorVisualizer;
import com.jorinvermeulen.shoutzor.main.Utility;

public class Decoration extends Effect {
	
	protected List<Spatial> lazers;

	public Decoration(ShoutzorVisualizer visualizer) {
		super(visualizer);

        //Background box
        Box 		b = new Box(30f, 0.1f, 30f);
        Box			pillarBaseBox = new Box(2.6f, 3f, 2.6f);
        Box			pillarMiddle = new Box(2.5f, 20f, 2.5f);
        Box			wall = new Box(30f, 20f, 0.1f);
        Box			wall2 = new Box(0.1f, 20f, 30f);
        Box			lineLeftBox = new Box(25f, 0.5f, 0.1f);
        Box			lineRightBox = new Box(0.1f, 0.5f, 25f);
        
        Material floor = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor.setColor("Color", Utility.hexToRGBA("#222222"));
        
        Material pillarBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pillarBase.setColor("Color", ColorRGBA.Magenta);
        
        Material pillarMiddleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pillarMiddleMat.setColor("Color", Utility.hexToRGBA("#666666"));
        
        Material lineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lineMaterial.setColor("Color", ColorRGBA.White);
        
        //Floor
        Spatial c = new Geometry("Box", b);
        c.setMaterial(floor);
        c.setLocalTranslation(0f, 0f, 0f);
        
        //Back Wall
        Spatial w1 = new Geometry("Box", wall);
        w1.setMaterial(floor);
        w1.setLocalTranslation(0f, 12f, -25f);
        w1.addMatParamOverride(new MatParamOverride(VarType.Vector4, "Color", Utility.hexToRGBA("#333333")));
        
        //Right Wall
        Spatial w2 = new Geometry("Box", wall2);
        w2.setMaterial(floor);
        w2.setLocalTranslation(25f, 12f, 0f);
        w2.addMatParamOverride(new MatParamOverride(VarType.Vector4, "Color", Utility.hexToRGBA("#333333")));
        
        //Pillar top-left
        Spatial pb1 = new Geometry("Box", pillarBaseBox);
        pb1.setMaterial(pillarBase);
        pb1.setLocalTranslation(-23.5f, 3f, -22.5f);
        Spatial pm1 = new Geometry("Box", pillarMiddle);
        pm1.setMaterial(pillarMiddleMat);
        pm1.setLocalTranslation(-23.5f, 12f, -22.5f);
        
        //Pillar top-right
        Spatial pb2 = new Geometry("Box", pillarBaseBox);
        pb2.setMaterial(pillarBase);
        pb2.setLocalTranslation(22.5f, 3f, -22.5f);
        Spatial pt2 = new Geometry("Box", pillarBaseBox);
        pt2.setMaterial(pillarBase);
        pt2.setLocalTranslation(22.5f, 29f, -22.5f);
        Spatial pm2 = new Geometry("Box", pillarMiddle);
        pm2.setMaterial(pillarMiddleMat);
        pm2.setLocalTranslation(22.5f, 12f, -22.5f);
        
        //Pillar bottom-right
        Spatial pb3 = new Geometry("Box", pillarBaseBox);
        pb3.setMaterial(pillarBase);
        pb3.setLocalTranslation(22.5f, 3f, 23.5f);
        Spatial pm3 = new Geometry("Box", pillarMiddle);
        pm3.setMaterial(pillarMiddleMat);
        pm3.setLocalTranslation(22.5f, 12f, 23.5f);
        
        //Left wall - Line along the bottom
        Spatial llb = new Geometry("Box", lineLeftBox);
        llb.setMaterial(lineMaterial);
        llb.setLocalTranslation(0f, 0f, -24.9f);
        
        //Left wall - Line along the Top
        Spatial llt = new Geometry("Box", lineLeftBox);
        llt.setMaterial(lineMaterial);
        llt.setLocalTranslation(0f, 31.2f, -24.9f);
        
        //Right wall - Line along the bottom
        Spatial rlb = new Geometry("Box", lineRightBox);
        rlb.setMaterial(lineMaterial);
        rlb.setLocalTranslation(24.9f, 0f, 0f);
        
        //Right wall - Line along the Top
        Spatial rlt = new Geometry("Box", lineRightBox);
        rlt.setMaterial(lineMaterial);
        rlt.setLocalTranslation(24.9f, 31.2f, 0f);
        
        rootNode.attachChild(c);
        rootNode.attachChild(w1);
        rootNode.attachChild(w2);
        rootNode.attachChild(pb1);
        rootNode.attachChild(pm1);
        rootNode.attachChild(pb2);
        rootNode.attachChild(pt2);
        rootNode.attachChild(pm2);
        rootNode.attachChild(pb3);
        rootNode.attachChild(pm3);
        rootNode.attachChild(llb);
        rootNode.attachChild(llt);
        rootNode.attachChild(rlb);
        rootNode.attachChild(rlt);
	}

	@Override
	public void draw(float tpf) {
	}
}
