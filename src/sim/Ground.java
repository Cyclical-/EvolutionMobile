package sim;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class Ground{

    private World world;

    private float segmentHeight = 0.2f;
    private float segmentLength = 1.0f;

    public static int maxSegments = 300;

    private ArrayList<Vec2> newCoordinates;

    public Ground(World world) {
        this.world = world;
    }

    /**
     * createGround
     * @author Jonah Shapiro
     * @description Randomly generates a set of tiles
     */
    public void createGround(){
        Vec2 tilePos = new Vec2(0, -0.5f); //the initial tile position
        //the following code generates 4 flat tiles to ensure a fair start
        for (int k = 0; k < 4; k++){
            Body start = newTile(tilePos, 0f); //create the first tile
            tilePos = start.getWorldPoint(this.newCoordinates.get(3)); //get the next tile coordinates
        }
        for (int i = 0; i < maxSegments-4; i++){
            Body previousTile = newTile(tilePos, (float)((Util.nextFloat(-10f, 8f) * 8f / 100) * Math.pow(-1, i))); //generate a new tile
            tilePos = previousTile.getWorldPoint(this.newCoordinates.get(3)); //get the next tile coordinates
        }

    }


    /**
     * newTile
     * @author Jonah Shapiro
     * @description creates a new tile
     * @param pos the position of this tile
     * @param angle the angle of this tile
     * @return the newly created tile body
     */
    private Body newTile(Vec2 pos, float angle){
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(new Vec2(pos.x, pos.y));
        Body body = world.createBody(bodyDef); //create the tile body

        Vec2[] coordinates = { //create the initial tile coordinates
                new Vec2(0,0),
                new Vec2(0, -segmentHeight),
                new Vec2(segmentLength, -segmentHeight),
                new Vec2(segmentLength, 0)
        };
        Vec2 center = new Vec2(0, 0);
        
        newCoordinates = rotate(coordinates, center, angle); //rotate the tile coordinates based on the angle
       
        PolygonShape segment = new PolygonShape();   
        segment.set(newCoordinates.toArray(new Vec2[0]), newCoordinates.size()); //add the rotated tile coordinates to the shape
        FixtureDef fixture = new FixtureDef();
        fixture.setFriction(0.5f);
        fixture.setShape(segment); //attach the shape to the fixture

        body.createFixture(fixture); //attach the fixture to the shape
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

    public void customGround(ArrayList<float[]> data) {
        Vec2 startPos = new Vec2(0, -0.5f);
        for (float[] angle : data) {
            Body tile = newTile(startPos, (float) (angle[0]));
            startPos = tile.getWorldPoint(this.newCoordinates.get(3));
        }
    }

}
