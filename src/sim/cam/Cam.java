package sim.cam;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
	public static float NEAR = 1f, FAR = 12f;
        public static final int DEFAULT_VERTICAL_FOV = 56;
        public static final int DEFAULT_HORIZONTAL_FOV = 69;

        public int vert_fov;
        public int horz_fov;
        
        public Cam(String name) {
            this(name, DEFAULT_VERTICAL_FOV);
        }
        public Cam(String name, int vert_fov) {
            this(name, vert_fov, DEFAULT_HORIZONTAL_FOV);
        }
	public Cam(String name, int vert_fov, int horz_fov) {
		this.main_node = new Node(name);
                
		// Camera
		this.camera = new Camera(RES_X, RES_Y);
		this.setVerticalFOV(vert_fov);
                this.horz_fov = horz_fov;
                // Geometry
		Material unshaded_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Misc/Unshaded.j3md");
		unshaded_mat.setColor("Color", ColorRGBA.Black);

		Box cam_body_box = new Box(1f, 1.5f , 4f);
		Geometry cam_body = new Geometry("CamBody" + name, cam_body_box);
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
            // Update Frustum
            CamControl control = this.main_node.getControl(CamControl.class);
            control.updateFrustum();
            
            if (Main.show_cam_point) {
                Debug.attachArrow(this.getMainNode(), this.getCamera().getDirection().mult((Cam.FAR + Cam.NEAR) / 2), ColorRGBA.LightGray, "Point_" + this.getMainNode().getName());
            } else {
                Debug.clearNode("Point", this.main_node);
            }
                
            // Makeshift frustum
            if (Main.show_cam_frust) {
                Vector3f fov = this.getCamera().getDirection().mult(Cam.FAR);

                Quaternion up_fov = new Quaternion();
                up_fov.fromAngleAxis(FastMath.DEG_TO_RAD * (this.vert_fov / 2), this.camera.getDirection().cross(this.camera.getUp()));
                Quaternion down_fov = new Quaternion();
                down_fov.fromAngleAxis(FastMath.DEG_TO_RAD * (-this.vert_fov / 2), this.camera.getDirection().cross(this.camera.getUp()));

                Quaternion rot_fov = new Quaternion();
                rot_fov.fromAngleAxis(FastMath.DEG_TO_RAD * (this.horz_fov / 2), this.camera.getUp());
                Vector3f rtfov = up_fov.mult(fov);
                rtfov = rot_fov.mult(rtfov);
                Vector3f rbfov = down_fov.mult(fov);
                rbfov = rot_fov.mult(rbfov);

                rot_fov.fromAngleAxis(FastMath.DEG_TO_RAD * (-this.horz_fov / 2), this.camera.getUp());
                Vector3f ltfov = up_fov.mult(fov);
                ltfov = rot_fov.mult(ltfov);
                Vector3f lbfov = down_fov.mult(fov);
                lbfov = rot_fov.mult(lbfov);

                ColorRGBA fov_color = (this.main_node.getName().contains("1")) ? ColorRGBA.Cyan : ColorRGBA.Magenta;
                Debug.attachArrow(this.main_node, rtfov, fov_color, "Frust_" + this.main_node.getName() + "_TRFOV");
                Debug.attachArrow(this.main_node, ltfov, fov_color, "Frust_" + this.main_node.getName() + "_TLFOV");
                Debug.attachArrow(this.main_node, rbfov, fov_color, "Frust_" + this.main_node.getName() + "_BRFOV");
                Debug.attachArrow(this.main_node, lbfov, fov_color, "Frust_" + this.main_node.getName() + "_BLFOV");
            } else {
                Debug.clearNode("Frust", this.main_node);
            }
	}


}
