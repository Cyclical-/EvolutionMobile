package sim;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;

import java.security.SecureRandom;

public class Util {


    public static float nextFloat(float minValue, float maxValue) {
        return MathUtils.randomFloat(minValue, maxValue);
    }

    public static int nextInt(int minValue, int maxValue){
        return (new SecureRandom().nextInt(maxValue - minValue)) + minValue;
    }


    public static double nextDouble(double minValue, double maxValue){
        return (new SecureRandom().nextDouble() * (maxValue - minValue)) + minValue;
    }

    public static Vec2 polarToRectangular(float magnitude, float angle){
        float x = magnitude * MathUtils.cos(angle);
        float y = magnitude * MathUtils.sin(angle);
        return new Vec2(x, y);
    }

    public static float[] rectangularToPolar(Vec2 point){
        float[] polar = new float[2]; //0 = magnitude, 1 = angle
        polar[0] = (float) Math.hypot(point.x, point.y);
        polar[1] = MathUtils.atan2(point.y, point.x);
        return polar;
    }

    public static float toPixelX(float x) {
        return x * 50f;
    }

    public static float toPixelY(float y) {
        return 600 - y * 50f;
    }

    public static boolean isDouble(String input) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
