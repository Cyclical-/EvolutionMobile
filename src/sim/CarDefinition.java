package sim;

import org.jbox2d.common.Vec2;

public class CarDefinition {
	
	private static final float MOTOR_SPEED = 20F;
	
	//chassis vector properties
	private static final float MIN_ANGLE = 0F; 
	private static final float MAX_ANGLE = (float) (Math.PI * 2);
	private static final float MIN_MAGNITUDE = 0.1F;
	private static final float MAX_MAGNITUDE = 1F;
	
	
	//wheel properties
	private static final float MIN_WHEEL_RADIUS = 0.1F;
	private static final float MAX_WHEEL_RADIUS = 1.0F;
	private static final float WHEEL_DENSITY = 60F;
	
	public WheelDefinition wheel1;
	public WheelDefinition wheel2;
	public WheelDefinition wheel3;
	

	public CarDefinition() {

	}
	
	
	public class WheelDefinition {
		private float radius;
		private float density;
		private int vertex;
		
		
		public float getRadius() {
			return radius;
		}
		public void setRadius(float radius) {
			this.radius = radius;
		}
		public float getDensity() {
			return density;
		}
		public void setDensity(float density) {
			this.density = density;
		}
		public int getVertex() {
			return vertex;
		}
		public void setVertex(int vertex) {
			this.vertex = vertex;
		}
		
		
	}

}
