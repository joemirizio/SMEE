package sim.cam;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.shadow.ShadowUtil;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import sim.Debug;
import sim.Main;

/**
 *
 * @author joemirizio
 */
public class CamControl extends AbstractControl {
	//Any local variables should be encapsulated by getters/setters so they
	//appear in the SDK properties window and can be edited.
	//Right-click a local variable to encapsulate it with getters and setters.

	private Camera camera;
	private Geometry frustum;
	private Vector3f[] frustum_pts;
	
	private WireFrustum wire_frustum;
	
	public CamControl(Camera camera, Node node) {
		this.camera = camera;
		
		frustum_pts = new Vector3f[8];
		for (int i = 0; i < frustum_pts.length; i++) { frustum_pts[i] = new Vector3f(); }
		//ShadowUtil
		this.updateFrustumPoints2(camera, frustum_pts);
		wire_frustum = new WireFrustum(frustum_pts);
		
		//@TODO Move to new Frustum class
		short[] textureCoords = new short[]{
					0, 0,
					0, 1,
					1, 1,
					1, 0,

					0, 0,
					1, 0,
					1, 1,
					0, 1,

					0, 0,
					1, 0,
					1, 1,
					0, 1,
		};
		
		//wire_frustum.setBuffer(Type.TexCoord, 2, textureCoords);
		//wire_frustum.updateBound();
		//wire_frustum.setMode(Mode.Triangles);
		
		
		this.frustum = new Geometry("Frustum " + camera.getName(), wire_frustum);
		this.frustum.setCullHint(Spatial.CullHint.Never);
		this.frustum.setShadowMode(ShadowMode.Off);
		this.frustum.setMaterial(new Material(Main.ASSET_MANAGER, "Common/MatDefs/Misc/Unshaded.j3md"));
		this.frustum.getMaterial().setColor("Color", ColorRGBA.White);
		
		//Main.ROOT_NODE.attachChild(this.frustum);
	}
	
	//@TODO Move to Cam
	public void updateFrustum() {
		// Draw camera frustum
		this.updateFrustumPoints2(camera, frustum_pts);
		wire_frustum.update(frustum_pts);
	}
	
        public void removeFrustum() {
            Main.ROOT_NODE.detachChild(this.frustum);
        }
        
	@Override
	protected void controlUpdate(float tpf) {
		
		//this.frustum.setLocalTranslation(camera.getLocation());
		//this.frustum.lookAt(camera.getDirection(), Vector3f.UNIT_Y);
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		//Only needed for rendering-related operations,
		//not called when spatial is culled.
	}
	
	public Control cloneForSpatial(Spatial spatial) {
		CamControl control = new CamControl(null, null);
		//TODO: copy parameters to new Control
		control.setSpatial(spatial);
		return control;
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		super.read(im);
		InputCapsule in = im.getCapsule(this);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule out = ex.getCapsule(this);
	}
	
	public final void updateFrustumPoints2(Camera viewCam, Vector3f[] points){
        int w = viewCam.getWidth();
        int h = viewCam.getHeight();
        float n = 0;//viewCam.getFrustumNear(); //0
        float f = 1;//viewCam.getFrustumFar(); // 1
        
        points[0].set(viewCam.getWorldCoordinates(new Vector2f(0, 0), n));
        points[1].set(viewCam.getWorldCoordinates(new Vector2f(0, h), n));
        points[2].set(viewCam.getWorldCoordinates(new Vector2f(w, h), n));
        points[3].set(viewCam.getWorldCoordinates(new Vector2f(w, 0), n));
        
        points[4].set(viewCam.getWorldCoordinates(new Vector2f(0, 0), f));
        points[5].set(viewCam.getWorldCoordinates(new Vector2f(0, h), f));
        points[6].set(viewCam.getWorldCoordinates(new Vector2f(w, h), f));
        points[7].set(viewCam.getWorldCoordinates(new Vector2f(w, 0), f));
    }
}
