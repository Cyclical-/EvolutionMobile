package sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbox2d.common.Vec2;

/**
 * CarDefinition
 * @author Jonah Shapiro
 * @description This class defines a car object and generates new cars
 */
public class CarDefinition {
	
	public static final float MOTOR_SPEED = 20F;
	
	//chassis vector properties
	private static final float MIN_ANGLE = 0F; 
	private static final float MAX_ANGLE = (float) (Math.PI * 2);
	private static final float MIN_MAGNITUDE = 0.1F;
	private static final float MAX_MAGNITUDE = 1.0F;
	private static final double MAGNITUDE_MULTIPLIER = 3.0;
	public static final int NUM_VERTICES = 8;
	public static final float CHASSIS_DENSITY = 100F;
	
	private ArrayList<Vec2> vertices;
	
	//wheel properties
	private static final float MIN_WHEEL_RADIUS = 0.1F;
	private static final float MAX_WHEEL_RADIUS = 0.5F;
	public static final float WHEEL_DENSITY = 60F;
	public static final int NUM_WHEELS = 3;

	
	private ArrayList<WheelDefinition> wheels;
	

	public CarDefinition() {
		this.vertices = new ArrayList<Vec2>();
		this.wheels = new ArrayList<WheelDefinition>();
	}
	
	public CarDefinition(ArrayList<Vec2> vertices, ArrayList<WheelDefinition> wheel){
		this.vertices = vertices;
		this.wheels = wheel;
	}
	
	public void addVertex(Vec2 vertex){
		this.vertices.add(vertex);
	}

	
	public ArrayList<Vec2> getVertices(){
		return this.vertices;
	}
	
	public void addWheel(WheelDefinition wheel){
		this.wheels.add(wheel);
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
	public static CarDefinition createRandomCar(){
		ArrayList<Vec2> vertices = new ArrayList<Vec2>();
		ArrayList<WheelDefinition> wheels = new ArrayList<WheelDefinition>();
		CarDefinition def = new CarDefinition();
		//generate chassis vectors
		vertices.add(new Vec2(0,0));
		for (int i = 0; i < NUM_VERTICES; i++){
			float angle = Util.nextFloat(MIN_ANGLE, MAX_ANGLE);
			float magnitude = Util.nextFloat(MIN_MAGNITUDE, MAX_MAGNITUDE);
			vertices.add(Util.polarToRectangular(magnitude, angle));
		}
		List<Integer> left = Stream.of(-1,-1,-1,0,1,2,3,4,5,6,7).collect(Collectors.toList());
		Random r = new Random();
		//generate wheels
		for (int w = 0; w < NUM_WHEELS; w++){
			int vertex = left.remove(r.nextInt(left.size()));
			float radius = Util.nextFloat(MIN_WHEEL_RADIUS, MAX_WHEEL_RADIUS);
			float density = WHEEL_DENSITY;
			wheels.add(def.new WheelDefinition(radius, density, vertex));
		}
		
		def.vertices = vertices;
		def.wheels = wheels;
		return def;
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
