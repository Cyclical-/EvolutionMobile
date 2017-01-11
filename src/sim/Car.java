package sim;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import sim.CarDefinition.WheelDefinition;

public class Car {
	
	private static final int MAX_CAR_HEALTH = Simulation.BOX2D_FPS * 10;
	
	private Body chassis;
	private Body wheel1;
	private Body wheel2;
	private Body wheel3;
	private World world;
	

    private int health = MAX_CAR_HEALTH;
    private float maxPosition = 0F;
    private float maxPositiony = 0F;
    private float minPositiony = 0F;

    public int frames = 0;

    public boolean alive = true;

    public CarDefinition definition;	
    
	private float[] genome;

	public double score;

	public Car(CarDefinition carDefinition, World world) {
		this.world = world;
		this.definition = carDefinition;
		writeGenome();
		this.genome = new float[22];
	}
	
	/**
	 * writeGenome
	 * @description This method accesses all of the definitions and uses their values to 
	 * @return the array containing the genome of the car
	 */
	public void writeGenome(){
		//write chassis
		ArrayList<Vec2> vertices = this.definition.getVertices();
		vertices.remove(0);
		for (int i = 0; i < vertices.size(); i++){
			float[] polar = Util.rectangularToPolar(vertices.get(i));
			this.genome[i*2] = polar[0];
			this.genome[(i*2)+1] = polar[1];
		}
		//write wheels
		ArrayList<WheelDefinition> wheels = this.definition.getWheels();
 		for (int i = 0; i < wheels.size(); i++){
			this.genome[(i*2)+(vertices.size()*2)] = wheels.get(i).getRadius();
			this.genome[(i*2)+1+(vertices.size()*2)] = wheels.get(i).getVertex();
		}
 		
	}
	
	
	public float[] getGenome(){
		return this.genome;
	}
	
	

}
