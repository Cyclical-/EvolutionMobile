package sim;

import java.security.SecureRandom;
import java.util.ArrayList;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class Ground{

	private World world;

	private float segmentHeight = 0.2f;
	private float segmentLength = 1.0f;

	private int maxSegments = 300;
	
	private ArrayList<Vec2> newCoordinates;

	public Ground(World world) {
		this.world = world;
	}
	
	public void createGround(){		
		Vec2 tilePos = new Vec2(0, -0.5f);
		for (int k = 0; k < 4; k++){
			Body start = newTile(tilePos, 0f);
			tilePos = start.getWorldPoint(this.newCoordinates.get(3));
		}
		for (int i = 0; i < maxSegments-4; i++){
			Body previousTile = newTile(tilePos, (float)((next(-10f, 8f) * 8f / 100) * Math.pow(-1, i)));
			tilePos = previousTile.getWorldPoint(this.newCoordinates.get(3));
		}
	}
	
	public float next(float minValue, float maxValue) {
        return (new SecureRandom().nextFloat() * (maxValue - minValue)) + minValue;
	}	
	
	private Body newTile(Vec2 pos, float angle){
		BodyDef bodyDef = new BodyDef();
		bodyDef.setPosition(new Vec2(pos.x, pos.y));
		Body body = world.createBody(bodyDef);
		
		Vec2[] coordinates = { //create the initial segment
							new Vec2(0,0),
							new Vec2(0, -segmentHeight),
							new Vec2(segmentLength, -segmentHeight),
							new Vec2(segmentLength, 0) 
							};
		Vec2 center = new Vec2(0, 0);
		newCoordinates = rotate(coordinates, center, angle);
		PolygonShape segment = new PolygonShape();
		
		segment.set(newCoordinates.toArray(new Vec2[0]), newCoordinates.size());
		FixtureDef fixture = new FixtureDef();
		fixture.setFriction(0.5f);
		fixture.setShape(segment);
		
		body.createFixture(fixture);
		
		return body;
		
		
		}


	/**
	 * rotate
	 * @author Jonah Shapiro
	 * @param coords the starting coordinates of the segment
	 * @param center the coordinates of the center
	 * @param angle the angle to rotate
	 * @return the adjusted segment coordinates
	 */
	private ArrayList<Vec2> rotate(Vec2[] coords, Vec2 center, float angle) {
		ArrayList<Vec2> newcoords = new ArrayList<Vec2>();
		for (int k = 0; k < coords.length; k++) {
			Vec2 nc = new Vec2();
			nc.x = new Float(Math.cos(angle) * (coords[k].x - center.x) - Math.sin(angle) * (coords[k].y - center.y) + center.x);
			nc.y = new Float(Math.sin(angle) * (coords[k].x - center.x) + Math.cos(angle) * (coords[k].y - center.y) + center.y);
			newcoords.add(nc);
		}
		return newcoords;
	}

}
