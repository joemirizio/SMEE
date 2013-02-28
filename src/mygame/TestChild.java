package mygame;

/**
 *
 * @author joemirizio
 */
public class TestChild {

	public static float speed_multiplier = 10;
	
	static enum Type {
		SlowRadial(0.2f),
		FastRadial(0.6f),
		TangentialArc(0.1f); //0.8f
		
		private final float speed;
		private Type(float speed) {
			this.speed = speed;
		}
		public float getSpeed() { return this.speed * speed_multiplier; }
	}
	
}