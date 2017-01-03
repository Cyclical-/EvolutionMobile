package sim;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

public class Car {
	
	private Body chassis;
	private Body wheel1;
	private Body wheel2;
	private Body wheel3;
	private World world;
	
	public CarDefinition definition;
	
	private float[] genome;
	
	public Car(CarDefinition carDefinition, World world) {
		this.world = world;
		this.definition = carDefinition;
		this.genome = new float[22];
	}
	
	/**
	 * writeGenome
	 * @description This method accesses all of the definitions and uses their values to 
	 * @return the array containing the genome of the car
	 */
	public float[] writeGenome(){
		
	}
	
	
	public float[] getGenome(){
		return this.genome;
	}
	
	

}
