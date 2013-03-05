package sim.rover;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author joemirizio
 */
public class TestChildControl extends AbstractControl {
	//Any local variables should be encapsulated by getters/setters so they
	//appear in the SDK properties window and can be edited.
	//Right-click a local variable to encapsulate it with getters and setters.

	public static final float END_DISTANCE = 13;
	
	protected float speed;
	protected float initial_angle;
	
	public TestChildControl(float speed) {
		this(speed, 0);
	}
	public TestChildControl(float speed, float angle) {
		this.speed = speed;
		this.initial_angle = angle;
	}
	
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		
		if (spatial != null) {
			this.turnRight(this.initial_angle);
		}
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		float angle = this.spatial.getLocalRotation().toAngles(null)[1];//this.spatial.getLocalRotation().toAngleAxis(Vector3f.UNIT_Y);
		float distance = speed * tpf;
		this.spatial.move(FastMath.sin2(angle) * distance, 0, FastMath.cos2(angle) * distance);
	
		if (this.getSpatialDistanceFromOrigin() >= END_DISTANCE) {
			this.spatial.removeFromParent();
			this.spatial.removeControl(this);
		}
	}
	
	protected final void turnRight(float angle) {
		this.turnLeft(-angle);
	}
	
	protected final void turnLeft(float angle) {
		this.spatial.rotate(0, angle, 0);
	}
	
	protected final float getSpatialDistanceFromOrigin() {
		return this.spatial.getLocalTranslation().distance(Vector3f.ZERO);
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		//Only needed for rendering-related operations,
		//not called when spatial is culled.
	}
	
	public Control cloneForSpatial(Spatial spatial) {
		TestChildControl control = new TestChildControl(0);
		//TODO: copy parameters to new Control
		control.setSpatial(spatial);
		return control;
	}
	
	@Override
	public void read(JmeImporter im) throws IOException {
		super.read(im);
		InputCapsule in = im.getCapsule(this);
		//TODO: load properties of this Control, e.g.
		//this.value = in.readFloat("name", defaultValue);
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule out = ex.getCapsule(this);
		//TODO: save properties of this Control, e.g.
		//out.write(this.value, "name", defaultValue);
	}
}
