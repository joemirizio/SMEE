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
	public static float NEAR = 2.62467f, FAR = 13.1234f;
        public static final int DEFAULT_VERTICAL_FOV = 56;

        public int vert_fov;
        
        public Cam(String name) {
            this(name, DEFAULT_VERTICAL_FOV);
        }
	public Cam(String name, int vert_fov) {
		this.main_node = new Node(name);
                
		// Camera
		this.camera = new Camera(RES_X, RES_Y);
		this.setVerticalFOV(vert_fov);
                // Geometry
		Material unshaded_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Misc/Unshaded.j3md");
		unshaded_mat.setColor("Color", ColorRGBA.Black);

		Box cam_body_box = new Box(1f, 1.5f , 4f);
		Geometry cam_body = new Geometry("CamBody", cam_body_box);
		cam_body.scale(Main.INCH_PER_FOOT);
		cam_body.setMaterial(unshaded_mat);
		this.main_node.attachChild(cam_body);

                this.resetCameraControl();
	}
        
        public void setVerticalFOV(int vert_fov) {
            this.vert_fov = vert_fov;
            this.camera.setFrustumPerspective(this.vert_fov, RES_X / RES_Y, NEAR, FAR); // 56 VFOV, 69 horizontal FOV
            this.resetCameraControl();
        }
        
        public CamControl resetCameraControl() {
            CamControl cam_ctrl = this.main_node.getControl(CamControl.class);
            if (cam_ctrl != null) {
                cam_ctrl.removeFrustum();
                this.main_node.removeControl(CamControl.class);
            }
            
            CamControl cam_control = new CamControl(this.camera, this.main_node);
            this.main_node.addControl(cam_control);
            return cam_control;
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
		Debug.attachArrow(this.getMainNode(), this.getCamera().getDirection().mult((Cam.FAR + Cam.NEAR) / 2), ColorRGBA.LightGray, this.getMainNode().getName() + "Loc");

		// Update Frustum
		CamControl control = this.main_node.getControl(CamControl.class);
		control.updateFrustum();
	}


}
