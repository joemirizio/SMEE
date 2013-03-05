package sim.rover;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author joemirizio
 */
public class TangenialArcControl extends TestChildControl {
	
	public static final float RIGHT_TURN_DIST = 6f;
	public static final float LEFT_TURN_DIST = 8f;

	public static enum State { radial1, radial2, radial3 }
	
	private State state;
	
	public TangenialArcControl(float speed) {
		this(speed, 0);
	}
	public TangenialArcControl(float speed, float angle) {
		super(speed, angle);
		this.state = State.radial1;
	}
	
	@Override
	protected void controlUpdate(float tpf) {		
		float dist = this.spatial.getLocalTranslation().distance(Vector3f.ZERO);
		switch (this.state) {
			case radial1: 
				if (dist > RIGHT_TURN_DIST) {
					this.turnRight(FastMath.HALF_PI);
					this.state = State.radial2;
				}
			break;
			case radial2:
				if (dist > LEFT_TURN_DIST) {
					this.turnLeft(FastMath.HALF_PI);
					this.state = State.radial3;
				}
			break;
		}
		
		super.controlUpdate(tpf);
	}
}
