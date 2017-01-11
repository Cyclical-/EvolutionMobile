package sim;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import sim.Ground;

import java.util.ArrayList;

public class MainWindow extends Application {

    public static final Vec2 GRAVITY = new Vec2(0.0F, -9.81F);
    private World world = new World(GRAVITY);
    public static final int FPS = 60;

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("EvolutionMobile");
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        Group root = new Group();

        Ground ground = new Ground(world);
        ground.createGround();
        Body body = world.getBodyList();

        while (body != null) {
            float x1 = toPixelX(body.getPosition().x);
            float y1 = toPixelY(body.getPosition().y);
            Line line = new Line();
            line.setStartX(x1);
            line.setStartY(y1);
            body = body.getNext();
            float x2 = 0;
            float y2 = 375;
            if (body != null) {
                x2 = toPixelX(body.getPosition().x);
                y2 = toPixelY(body.getPosition().y);
            }
            line.setEndX(x2);
            line.setEndY(y2);
            root.getChildren().add(line);
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    
    /**
     * run
     * 
     */
    public void run() throws InterruptedException{
    	while(true){
    		Thread.sleep(1000/FPS);
    		update();
    		show();
    	}
    }
    
    public void update(){
    	
    }
    
    public void show(){
    	
    }

    public static float toPixelX(float x) {
        return x * 50f + 200f;
    }

    public static float toPixelY(float y) {
        return HEIGHT - 250f - y * 50f;
    }

    public static float toPixelWidth(float width) {
        return width * 50f;
    }

    public static float toPixelHeight(float height) {
        return height * 50f;
    }

    public void rouletteSelection(ArrayList<Car> currentGen){

        //fitnessScores - index 0 is the car's fitness score - index 1 is the car's probability of selection
        double [][] fitnessScores = new double[20][2];
        int count = 0;
        for (Car car: currentGen){
            fitnessScores[count][0] = car.getFitnessScore();
            count++;

        }

        //Find sum of all fitness scores
        double sumOfFitnessScores = 0;
        for (int i = 0; i < fitnessScores.length; i++){
            sumOfFitnessScores = fitnessScores[i][0] + sumOfFitnessScores;
        }

        //Find each car's probability of selection
        for (int i = 0; i < fitnessScores.length; i++){
            fitnessScores[i][1] = (fitnessScores[i][0] / sumOfFitnessScores)*100;
        }

        double[] rouletteWheel = new double[20];
        rouletteWheel[0] = fitnessScores[0][1];
        for (int i = 1; i < rouletteWheel.length; i++){
            rouletteWheel[i] = fitnessScores[i][1] + rouletteWheel[i-1];
        }

        //selecting parents
        double selectionNum;
        ArrayList<Car> parents = new ArrayList<>();
        do{
            selectionNum = ((double) Math.random()*101);

            if ((selectionNum >= 0) && (selectionNum <= rouletteWheel[0])){
                parents.add()
            }
            for (int j = )
        }while (parents.size() < 10);

    }

    public void crossover (){

    }

    public void mutation(){

    }

    public void createChild(float[] genome){

        new Car(genome);
    }


    public static void main(String[] args) {
        launch(args);
    }

}