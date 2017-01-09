package sim;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

/**
 * CarDefinition
 * @author Jonah Shapiro
 * @description This class defines a car object and generates new cars
 */
public class CarDefinition {
	
	private static final float MOTOR_SPEED = 20F;
	
	//chassis vector properties
	private static final float MIN_ANGLE = 0F; 
	private static final float MAX_ANGLE = (float) (Math.PI * 2);
	private static final float MIN_MAGNITUDE = 0.1F;
	private static final float MAX_MAGNITUDE = 1F;
	private static final double MAGNITUDE_MULTIPLIER = 3.0;
	private static final int NUM_VERTICES = 8;
	
	private ArrayList<Vec2> vertices;
	
	//wheel properties
	private static final float MIN_WHEEL_RADIUS = 0.1F;
	private static final float MAX_WHEEL_RADIUS = 1.0F;
	private static final float WHEEL_DENSITY = 60F;
	private static final int NUM_WHEELS = 3;

	
	public ArrayList<WheelDefinition> wheels;
	

	public CarDefinition() {
		this.vertices = new ArrayList<Vec2>();
		this.wheels = new ArrayList<WheelDefinition>();
	}
	
	public ArrayList<Vec2> getVertices(){
		return this.vertices;
	}
	
	public ArrayList<WheelDefinition> getWheels(){
		return this.wheels;
	}
	
	/**
	 * createRandomCar
	 * @author Jonah Shapiro
	 * @return a CarDefinition containing a randomly generated car
	 * 
	 */
	public CarDefinition createRandomCar(){
		//generate chassis vectors
		this.vertices.add(new Vec2(0,0));
		for (int i = 0; i < NUM_VERTICES; i++){
			float angle = Util.nextFloat(MIN_ANGLE, MAX_ANGLE);
			float magnitude = Util.nextFloat(MIN_MAGNITUDE, MAX_MAGNITUDE);
			this.vertices.add(Util.polarToRectangular(magnitude, angle));
		}
		
		//generate wheels
		for (int w = 0; w < NUM_WHEELS; w++){
			float radius = Util.nextFloat(MIN_WHEEL_RADIUS, MAX_WHEEL_RADIUS);
			float density = WHEEL_DENSITY;
			int vertex = Util.nextInt(-1, NUM_VERTICES-1);
			this.wheels.add(new WheelDefinition(radius, density, vertex));
		}
		
		return this;
	}
	
	
	/**
	 * WheelDefinition
	 * @author Jonah Shapiro
	 * @description This inner class defines a wheel to be attatched to the chassis
	 *
	 */
	public class WheelDefinition {
		private float radius;
		private float density;
		private int vertex;
		
		public WheelDefinition(float radius, float density, int vertex) {
			this.radius = radius;
			this.density = density;
			this.vertex = vertex;
		}
		
		public float getRadius() {
			return radius;
		}
		public float getDensity() {
			return density;
		}
		
		public int getVertex() {
			return vertex;
		}
		
		
	}

}