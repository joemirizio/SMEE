package sim.cam;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import sim.Debug;
import sim.Main;

/**
 *
 * @author joemirizio
 */
public class Cam {
	
	private Node main_node;
	private Camera camera;
	
	public static int RES_X = 640, RES_Y = 480;
	public static float NEAR = 2.62467f, FAR = 13.1234f; //from microsoft website, near and far planes. Kate to research
	public static int vFOV1 = 56;
        public static int vFOV2 = 43; 
        
	public Cam(String name) {//, int vFov) { //create variables here, need to add to main class
		this.main_node = new Node(name);
		
		// Camera
		this.camera = new Camera(RES_X, RES_Y);
		this.camera.setFrustumPerspective(vFOV1, RES_X / RES_Y, NEAR, FAR); // 56 VFOV, 69 horizontal FOV
		// Geometry
		Material unshaded_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Misc/Unshaded.j3md");
		unshaded_mat.setColor("Color", ColorRGBA.Black); //set color of cam box to black 
		
		Box cam_body_box = new Box(11f, 1.5f , 2.5f);
		Geometry cam_body = new Geometry("CamBody", cam_body_box);
		cam_body.scale(Main.INCH_PER_FOOT);
		cam_body.setMaterial(unshaded_mat);
		this.main_node.attachChild(cam_body);
		
		CamControl cam_control = new CamControl(this.camera, this.main_node);
		this.main_node.addControl(cam_control);
	}
	
	
	public Node getMainNode() {
		return this.main_node;
	}
	
	public Camera getCamera() {
		return this.camera;
	}
		
	public Cam setLocalTranslation(Vector3f location) {
		this.main_node.setLocalTranslation(location);
		this.camera.setLocation(location);
		return this;
	}
	
	public void lookAt(Vector3f location) {
		this.camera.lookAtDirection(location, Vector3f.UNIT_Y);
		Debug.attachArrow(this.getMainNode(), this.getCamera().getDirection().mult((Cam.FAR + Cam.NEAR) / 2), ColorRGBA.White, this.getMainNode().getName() + "Loc");
		
		// Update Frustum
		CamControl control = this.main_node.getControl(CamControl.class);
		control.updateFrustum();
	}
	
	
}
