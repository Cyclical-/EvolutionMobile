package sim;

import java.security.SecureRandom;

import org.jbox2d.common.Vec2;

public class Util {

	
	public static float nextFloat(float minValue, float maxValue) {
        return (new SecureRandom().nextFloat() * (maxValue - minValue)) + minValue;
	}	
	
	public static int nextInt(int minValue, int maxValue){
		return (new SecureRandom().nextInt() * (maxValue - minValue)) + minValue; 
	}
	
	
	public static double nextDouble(double minValue, double maxValue){
		return (new SecureRandom().nextDouble() * (maxValue - minValue)) + minValue; 
	}
	
	public static Vec2 polarToRectangular(float magnitude, float angle){	
		float x = (float) (magnitude * Math.cos(angle));
		float y = (float) (magnitude * Math.sin(angle));
		return new Vec2(x, y);
	}
	
	public static float[] rectangularToPolar(Vec2 point){
		float[] polar = new float[2]; //0 = magnitude, 1 = angle
		polar[0] = (float) Math.hypot(point.x, point.y);
		polar[1] = (float) Math.atan(point.x/point.y);
		return polar;
	}

}
