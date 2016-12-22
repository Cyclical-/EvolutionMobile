package sim;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Simulation {

	public static final int BOX2D_FPS = 60;
	public static final Vec2 GRAVITY = new Vec2(0.0F, -9.81F);
	public static final float TIMESTEP = 1f / BOX2D_FPS;

	private World world;
	
	public Simulation() {
		this.world = new World(GRAVITY);
		Ground ground = new Ground(this.world);
		ground.createGround();
		
	}
	
	public Simulation(World world){
		this.world = world;
		Ground ground = new Ground(this.world);
		ground.createGround();
	}
	
	

}
