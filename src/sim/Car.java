package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.smartcardio.Card;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import sim.CarDefinition.WheelDefinition;

public class Car {

	private static final int MAX_CAR_HEALTH = Simulation.BOX2D_FPS * 10;

	private Body chassis;
	private Body wheel1;
	private Body wheel2;
	private Body wheel3;
	private World world;
	private double score = 0;

	private int health = MAX_CAR_HEALTH;
	private float maxPosition = 0F;
	private float maxPositiony = 0F;
	private float minPositiony = 0F;

	public int frames = 0;

	public boolean alive = true;

	public CarDefinition definition;

	private float[] genome;
	
	private boolean selected;

	public Car(float[] genome, World world) {
		this.world = world;
		this.genome = genome;
		this.definition = createDefinition();
		writeGenome();
	}

	private CarDefinition createDefinition() {
		for (int i = 0; i < CarDefinition.NUM_VERTICES; i++) {

		}
	}

	/**
	 * writeGenome
	 * 
	 * @description This method accesses all of the definitions and uses their values to
	 * @return the array containing the genome of the car
	 */
	public void writeGenome() {
		// write chassis
		ArrayList<Vec2> vertices = this.definition.getVertices();
		vertices.remove(0);
		for (int i = 0; i < vertices.size(); i++) {
			float[] polar = Util.rectangularToPolar(vertices.get(i));
			this.genome[i * 2] = polar[0];
			this.genome[(i * 2) + 1] = polar[1];
		}
		// write wheels
		ArrayList<WheelDefinition> wheels = this.definition.getWheels();
		for (int i = 0; i < wheels.size(); i++) {
			this.genome[(i * 2) + (vertices.size() * 2)] = wheels.get(i).getRadius();
			this.genome[(i * 2) + 1 + (vertices.size() * 2)] = wheels.get(i).getVertex();
		}

	}

	private ArrayList<Float> sortAngles(ArrayList<Vec2> vertices) {
		ArrayList<Float> angles = new ArrayList<Float>();
		for (int i = 0; i < vertices.size(); i++) {
			float[] polar = Util.rectangularToPolar(vertices.get(i));
			angles.add(new Float(polar[1]));
		}
		Collections.sort(angles);
		return angles;
	}

	private Body createChassis(ArrayList<Vec2> vertices) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position = new Vec2(0.0F, 4.0F);
		Body body = world.createBody(bodyDef);
		HashMap<Float, Float> points = new HashMap<Float, Float>();
		for (int i = 0; i < vertices.size(); i++) {
			float[] polar = Util.rectangularToPolar(vertices.get(i));
			points.put(polar[1], polar[0]); //key is angle, value is magnitude
		}
		ArrayList<Float> sorted = sortAngles(vertices);
		ArrayList<Vec2> sortedVertices = new ArrayList<Vec2>();
		for (int i = 0; i < sorted.size(); i++){
			sortedVertices.add(Util.polarToRectangular(points.get(sorted.get(i)), sorted.get(i)));
		}
		//create chassis parts
		for (int part = 1; part < sortedVertices.size(); part++){
			createChassisPart(body, sortedVertices.get(i-1), sortedVertices.get(i));
		}
		return body;
	}

	private void createChassisPart(Body body, Vec2 one, Vec2 two) {
		ArrayList<Vec2> listOfVertices = new ArrayList<>(3);
		
		listOfVertices.add(one);
		listOfVertices.add(two);
		listOfVertices.add(new Vec2(0,0));
		
		FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = new PolygonShape();
        fixtureDef.density = this.definition.CHASSIS_DENSITY;
        fixtureDef.friction = 10F;
        fixtureDef.restitution = 0.2F;
        fixtureDef.filter.groupIndex = -1;
        ((PolygonShape)fixtureDef.shape).set(listOfVertices.toArray(new Vec2[0]), 3);
        body.createFixture(fixtureDef);
	}

	public float[] getGenome() {
		return this.genome;
	}
	
	public boolean getSelected(){
		return this.selected;
	}
	
	public void setSelected(){
		this.selected = true;
	}
	
	public double getFitnessScore(){
		return this.score;
	}

}
