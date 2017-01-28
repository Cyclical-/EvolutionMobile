package sim;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;

import java.security.SecureRandom;

/**
 * Util.java
 * @author Jonah Shapiro
 *
 */
public class Util {


    /**
     * nextFloat
     * @author Jonah Shapiro
     * @param minValue
     * @param maxValue
     * @return
     */
    public static float nextFloat(float minValue, float maxValue) {
        return MathUtils.randomFloat(minValue, maxValue);
    }

    /**
     * nextInt
     * @author Jonah Shapiro
     * @param minValue
     * @param maxValue
     * @return
     */
    public static int nextInt(int minValue, int maxValue){
        return (new SecureRandom().nextInt(maxValue - minValue)) + minValue;
    }


    /**
     * nextDouble
     * @author Jonah Shapiro
     * @param minValue
     * @param maxValue
     * @return
     */
    public static double nextDouble(double minValue, double maxValue){
        return (new SecureRandom().nextDouble() * (maxValue - minValue)) + minValue;
    }

    /**
     * polarToRectangular
     * @author Jonah Shapiro
     * @param magnitude
     * @param angle
     * @return
     */
    public static Vec2 polarToRectangular(float magnitude, float angle){
        float x = magnitude * MathUtils.cos(angle);
        float y = magnitude * MathUtils.sin(angle);
        return new Vec2(x, y);
    }

    /**
     * rectangularToPolar
     * @author Jonah Shapiro
     * @param point
     * @return
     */
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

    /**
     * isDouble
     * @author Jonah Shapiro
     * @param input
     * @return
     */
    public static boolean isDouble(String input) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * round2
     * @author Jonah Shapiro
     * @param number
     * @param scale
     * @return
     */
    public static float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    /**
     * isInt
     * @author Jonah Shapiro
     * @param input
     * @return
     */
    public static boolean isInt(String input) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
