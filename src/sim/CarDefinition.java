package sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;

/**
 * CarDefinition
 * 
 * @author Jonah Shapiro
 * @description This class defines a car object and generates new cars
 */
public class CarDefinition {

	public static final float MOTOR_SPEED = 20F;

	// chassis vector properties
	private static final float MIN_ANGLE = 0F;
	private static final float MAX_ANGLE = MathUtils.TWOPI;
	private static final float MIN_MAGNITUDE = 0.1F;
	private static final float MAX_MAGNITUDE = 1.0F;
	public static final int NUM_VERTICES = 8;
	public static final float CHASSIS_DENSITY = Util.nextFloat(100, 300);

	private ArrayList<Vec2> vertices;

	// wheel properties
	private static final float MIN_WHEEL_RADIUS = 0.1F;
	private static final float MAX_WHEEL_RADIUS = 0.3F;
	public static final int NUM_WHEELS = 3;

	private ArrayList<WheelDefinition> wheels;

	/**
	 * @author Jonah Shapiro
	 */
	public CarDefinition() {
		this.vertices = new ArrayList<Vec2>();
		this.wheels = new ArrayList<WheelDefinition>();
	}

	/**
	 * @param vertices
	 * @param wheel
	 */
	public CarDefinition(ArrayList<Vec2> vertices, ArrayList<WheelDefinition> wheel) {
		this.vertices = vertices;
		this.wheels = wheel;
	}

	/**
	 * addVertex
	 * @author Jonah Shapiro
	 * @param vertex
	 */
	public void addVertex(Vec2 vertex) {
		this.vertices.add(vertex);
	}

	/**
	 * getVertices
	 * @author Jonah Shapiro
	 * @return the arraylist of vertices
	 */
	public ArrayList<Vec2> getVertices() {
		return this.vertices;
	}

	/**
	 * addWheel
	 * @author Jonah Shapiro
	 * @param wheel the wheel to add
	 */
	public void addWheel(WheelDefinition wheel) {
		this.wheels.add(wheel);
	}

	/**
	 * getWheels
	 * @author Jonah Shapiro
	 * @return the arraylist of wheels
	 */
	public ArrayList<WheelDefinition> getWheels() {
		return this.wheels;
	}

	/**
	 * checkValid
	 * @author Jonah Shapiro
	 * @description This method checks for polygon degeneracy
	 * @param point The point to check
	 * @param vertices The other vertices to check against
	 * @return A boolean representing the validity of the given point
	 */
	public static boolean checkValid(Vec2 point, ArrayList<Vec2> vertices) {
		for (int i = 0; i < vertices.size(); i++) { //compare the given vertex to each other vertex
			if (!point.equals(vertices.get(i))) {
				if (MathUtils.distanceSquared(point, vertices.get(i)) < 0.5f * Settings.linearSlop) { //if the points are colinear
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * createRandomCar
	 * 
	 * @author Jonah Shapiro
	 * @return a CarDefinition containing a randomly generated car
	 * 
	 */
	public static CarDefinition createRandomCar() {
		ArrayList<Vec2> vertices = new ArrayList<Vec2>();
		ArrayList<WheelDefinition> wheels = new ArrayList<WheelDefinition>();
		CarDefinition def = new CarDefinition();
		// generate chassis vectors
		for (int i = 0; i < NUM_VERTICES; i++) {
			Vec2 point;
			do {
				float angle = Util.nextFloat(MIN_ANGLE, MAX_ANGLE); //generate a random angle
				float magnitude = Util.nextFloat(MIN_MAGNITUDE, MAX_MAGNITUDE); //generate a random magnitude
				point = Util.polarToRectangular(magnitude, angle); //convert the polar coords to rectangular coords
			} while (!checkValid(point, vertices)); //check for polygon degeneracy
			vertices.add(point);
		}
		List<Integer> left = Stream.of(-1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7).collect(Collectors.toList()); //use a stream to ensure two wheels do not share a vertex
		Random r = new Random();
		// generate wheels
		for (int w = 0; w < NUM_WHEELS; w++) {
			int vertex = left.remove(r.nextInt(left.size())); //get a random vertex
			float radius = Util.nextFloat(MIN_WHEEL_RADIUS, MAX_WHEEL_RADIUS); //generate a random radius
			float density = Util.nextFloat(50, 100); //generate a random density
			wheels.add(def.new WheelDefinition(radius, density, vertex)); //add the wheel to the definition
		}

		def.vertices = vertices;
		def.wheels = wheels;
		return def;
	}

	/**
	 * WheelDefinition
	 * 
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
