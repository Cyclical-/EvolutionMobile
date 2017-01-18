package sim;

import javafx.scene.paint.Color;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import sim.CarDefinition.WheelDefinition;

/**
 * 
 * @author Jonah Shapiro
 *
 */
public class Car {


	private static final int MAX_CAR_HEALTH = Simulation.BOX2D_FPS;

	private Body chassis;
	private ArrayList<Body> wheels;
	private ArrayList<Joint> joints;
	private World world;
	private double score = 0;

	private int health = MAX_CAR_HEALTH;
	private float maxPositionx = 0F;
	private float maxPositiony = 0F;
	private float minPositiony = 0F;

	public boolean alive;

	public CarDefinition definition;

	private float[] genome;

	private boolean selected;

	/**
	 * @param genome
	 * @param world
	 */
	public Car(CarDefinition def, World world) {
		this.world = world;
		this.genome = new float[22];
		this.definition = def;
		this.wheels = new ArrayList<Body>();
		this.joints = new ArrayList<Joint>();
		writeGenome();
		this.chassis = createChassis(this.definition.getVertices()); // create chassis
		float carMass = this.chassis.getMass();
		// create wheels
		for (int i = 0; i < this.definition.getWheels().size(); i++) {
			if (this.definition.getWheels().get(i).getVertex() != -1) {
				Body wheel = createWheel(this.definition.getWheels().get(i));
				this.wheels.add(wheel);
				carMass += wheel.getMass();
				this.joints.add(createJointForWheel(wheel, this.definition.getWheels().get(i), (carMass * (-Simulation.GRAVITY.y / this.definition.getWheels().get(i).getRadius()))));
			}
		}
		this.alive = true;

	}

	public Car(float[] genome, World world) {
		this.world = world;
		this.genome = genome;
		this.definition = createDefinition();
		this.wheels = new ArrayList<Body>();
		this.joints = new ArrayList<Joint>();
		this.chassis = createChassis(this.definition.getVertices()); // create chassis
		float carMass = this.chassis.getMass();
		// create wheels
		for (int i = 0; i < this.definition.getWheels().size(); i++) {
			if (this.definition.getWheels().get(i).getVertex() != -1) {
				Body wheel = createWheel(this.definition.getWheels().get(i));
				this.wheels.add(wheel);
				carMass += wheel.getMass();
				this.joints.add(createJointForWheel(wheel, this.definition.getWheels().get(i), (carMass * (-Simulation.GRAVITY.y / this.definition.getWheels().get(i).getRadius()))));
			}
		}
		this.alive = true;
	}

	/**
	 * createDefinition
	 * 
	 * @author Jonah Shapiro
	 * @return
	 */
	private CarDefinition createDefinition() {
		CarDefinition def = new CarDefinition();
		for (int i = 0; i < CarDefinition.NUM_VERTICES; i++) {
			def.addVertex(Util.polarToRectangular(this.genome[i * 2], this.genome[(i * 2) + 1]));
		}
		for (int w = 0; w < CarDefinition.NUM_WHEELS; w++){
			def.addWheel(def.new WheelDefinition(this.genome[(w*2) + (CarDefinition.NUM_VERTICES * 2)], Util.nextFloat(50, 100), (int)this.genome[(w*2) + 1 + (CarDefinition.NUM_VERTICES * 2)]));
		}
		return def;
	}

	/**
	 * writeGenome
	 * 
	 * @author Jonah Shapiro
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

	public boolean checkDeath() {
		Vec2 position = this.getPosition();

		if (position.y > this.maxPositiony) {
			this.maxPositiony = position.y;
		}

		if (position.y < minPositiony) {
			this.minPositiony = position.y;
		}
		
		if (position.x < 0.0F){
			return true;
		}
		if (position.x > 300){
			this.maxPositionx = 300;
			return true;

		}

		if (position.x > maxPositionx + 0.02f) {
			this.health = MAX_CAR_HEALTH;
			this.maxPositionx = position.x;
		} else {
			if (Math.abs(this.chassis.getLinearVelocity().x) < 0.01f) {
				this.health -= 2;
			}
			if (position.x > maxPositionx) {
				this.maxPositionx = position.x;
			}
			this.health--;
			if (this.health <= 0) {
				return true;
			}
		}
		return false;

	}

	public void kill() {
		for (Joint j : this.joints){
			this.world.destroyJoint(j);
		}
		for (Body wheel : this.wheels) {
			this.world.destroyBody(wheel);
		}
		this.world.destroyBody(this.chassis);
		this.alive = false;
	}

	/**
	 * createWheel
	 * 
	 * @author Jonah Shapiro
	 * @param wheelDefinition
	 * @return
	 */
	private Body createWheel(CarDefinition.WheelDefinition wheelDef) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position = new Vec2(0F, 0F);
		Body body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = new CircleShape();
		fixtureDef.shape.setRadius(wheelDef.getRadius());
		fixtureDef.density = wheelDef.getDensity();
		fixtureDef.friction = 1F;
		fixtureDef.restitution = 0.2F;
		fixtureDef.filter.groupIndex = -1;

		body.createFixture(fixtureDef);

		return body;
		
	}

	private Joint createJointForWheel(Body wheel, CarDefinition.WheelDefinition wheelDef, float torqueWheel) {
		RevoluteJointDef jointDefinition = new RevoluteJointDef();
		Vec2 randVec2 = this.definition.getVertices().get(wheelDef.getVertex());
		jointDefinition.bodyA = this.chassis;
		jointDefinition.bodyB = wheel;
		jointDefinition.localAnchorA = randVec2;
		jointDefinition.localAnchorB = new Vec2(0F, 0F);
		jointDefinition.maxMotorTorque = torqueWheel;
		jointDefinition.motorSpeed = -CarDefinition.MOTOR_SPEED;
		jointDefinition.enableMotor = true;
		return world.createJoint(jointDefinition);
	}

	/**
	 * createChassis
	 * 
	 * @author Jonah Shapiro
	 * @param vertices
	 * @return
	 */
	private Body createChassis(ArrayList<Vec2> vertices) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position = new Vec2(1.0F, 4.0F);
		Body body = world.createBody(bodyDef);
		TreeMap<Float, Float> points = new TreeMap<Float, Float>();
		for (int i = 0; i < vertices.size(); i++) {
			float[] polar = Util.rectangularToPolar(vertices.get(i));
			points.put(polar[1], polar[0]); // key is angle, value is magnitude
		}
		ArrayList<Vec2> sorted = new ArrayList<Vec2>();
		ArrayList<Float> keys = new ArrayList<Float>();
		for (Float f : points.keySet()){
			keys.add(f);
		}
		for (int i = 0; i < points.size(); i++) {
			sorted.add(Util.polarToRectangular(points.get(keys.get(i)), keys.get(i)));
		}
		// create chassis parts
		for (int part = 1; part  < sorted.size(); part++) {
			createChassisPart(body, sorted.get(part - 1), sorted.get(part));
		}
		return body;
	}


	/**
	 * createChassisPart
	 * 
	 * @author Jonah Shapiro
	 * @param body
	 * @param one
	 * @param two
	 */
	private void createChassisPart(Body body, Vec2 one, Vec2 two) {
		Vec2[] listOfVertices = new Vec2[3];
		listOfVertices[0] = one;
		listOfVertices[1] = two;
		listOfVertices[2] = new Vec2(0,0);
		PolygonShape s = new PolygonShape();
		s.set(listOfVertices, 3);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = s;
		fixtureDef.density = CarDefinition.CHASSIS_DENSITY;
		fixtureDef.friction = 10F;
		fixtureDef.restitution = 0.2F;
		fixtureDef.filter.groupIndex = -1;
		fixtureDef.setUserData(new Color(one.length(), two.length(), Util.nextDouble(0, 1), 0.5));
		body.createFixture(fixtureDef);
		
	}

	/**
	 * getPosition
	 * 
	 * @author Jonah Shapiro
	 * @return
	 */
	public Vec2 getPosition() {
		return chassis.getPosition();
	}

	/**
	 * getGenome
	 * 
	 * @author Jonah Shapiro
	 * @return
	 */
	public float[] getGenome() {
		return this.genome;
	}

	/**
	 * getSelected
	 * 
	 * @author Anthony Lai
	 * @return
	 */
	public boolean getSelected() {
		return this.selected;
	}

	/**
	 * setSelected
	 * 
	 * @author Anthony Lai
	 */
	public void setSelected() {
		this.selected = true;
	}

	/**
	 * getFitnessScore
	 * 
	 * @author Anthony Lai
	 * @return
	 */
	public double getFitnessScore() {
		return this.maxPositionx;
	}

}
