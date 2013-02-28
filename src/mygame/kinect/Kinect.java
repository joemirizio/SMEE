package mygame.kinect;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import mygame.Debug;
import mygame.Main;

/**
 *
 * @author joemirizio
 */
public class Kinect {
	
	private Node main_node;
	private Camera camera;
	
	public static int RES_X = 640, RES_Y = 480;
	public static float NEAR = 2.62467f, FAR = 13.1234f;
	
	public Kinect(String name) {
		this.main_node = new Node(name);
		
		// Camera
		this.camera = new Camera(RES_X, RES_Y);
		this.camera.setFrustumPerspective(43, RES_X / RES_Y, NEAR, FAR); // 57 horizontal FOV
		// Geometry
		Material unshaded_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Misc/Unshaded.j3md");
		unshaded_mat.setColor("Color", ColorRGBA.Black);
		
		Box kinect_body_box = new Box(11f, 1.5f , 2.5f);
		Geometry kinect_body = new Geometry("KinectBody", kinect_body_box);
		kinect_body.scale(Main.INCH_PER_FOOT);
		kinect_body.setMaterial(unshaded_mat);
		this.main_node.attachChild(kinect_body);
		
		KinectControl kinect_control = new KinectControl(this.camera, this.main_node);
		this.main_node.addControl(kinect_control);
	}
	
	
	public Node getMainNode() {
		return this.main_node;
	}
	
	public Camera getCamera() {
		return this.camera;
	}
		
	public Kinect setLocalTranslation(Vector3f location) {
		this.main_node.setLocalTranslation(location);
		this.camera.setLocation(location);
		return this;
	}
	
	public void lookAt(Vector3f location) {
		this.camera.lookAtDirection(location, Vector3f.UNIT_Y);
		Debug.attachArrow(this.getMainNode(), this.getCamera().getDirection().mult((Kinect.FAR + Kinect.NEAR) / 2), ColorRGBA.White, this.getMainNode().getName() + "Loc");
		
		// Update Frustum
		KinectControl control = this.main_node.getControl(KinectControl.class);
		control.updateFrustum();
	}
	
	
}
