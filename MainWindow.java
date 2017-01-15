package sim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import sim.Ground;

import java.util.ArrayList;

public class MainWindow extends Application {

    public static final Vec2 GRAVITY = new Vec2(0.0F, -9.81F);
    private World world = new World(GRAVITY);
    public static final int FPS = 60;

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    public static int deadCars = 0;

    private boolean north, south, east, west;
    private ArrayList<double[]> startShapes = new ArrayList<>();
    private Body[] bodyList;
    private ArrayList<Shape> shapeList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        //stage settings
        primaryStage.setTitle("EvolutionMobile");
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        //root
        Group root = new Group();

        //randomly generated terrain
        Ground ground = new Ground(world);
        ground.createGround();

        //get starting positions of ground
        getStartPos();

        //draw bodies
        draw(root);

        //create array for every body
        createBodyList();

        //create scene
        Scene scene = new Scene(root);

        //camera controls(key pressed)
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP: north = true; break;
                case DOWN: south = true; break;
                case LEFT: west = true; break;
                case RIGHT: east = true; break;
            }
        });

        //camera controls(key released)
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP: north = false; break;
                case DOWN: south = false; break;
                case LEFT: west = false; break;
                case RIGHT: east = false; break;
            }
        });

        //set scene
        primaryStage.setScene(scene);
        primaryStage.show();

        while (true) {


            //evaluate
            try {
                run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * run
     * sets up keyframes for every 1/60s
     * @throws InterruptedException if thread is interrupted
     */
    private void run() throws InterruptedException {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        Duration duration = Duration.seconds(1.0 / FPS);
        EventHandler<ActionEvent> actionEvent = terminate -> {
            world.step(1.0f / FPS, 8, 3);
            camera();
        };
        KeyFrame keyFrame = new KeyFrame(duration, actionEvent, null, null);
        timeline.getKeyFrames().add(keyFrame);
        timeline.playFromStart();
    }
    /**
     * update
     * moves shapes according to JBox2D world
     */
    private void camera() {
        if (north) {
            for(Shape shape: shapeList) {
                if (shape instanceof Line) {
                    ((Line)shape).setStartY(((Line)shape).getStartY() + 5);
                    ((Line)shape).setEndY(((Line)shape).getEndY() + 5);
                } else if (shape instanceof Circle) {
                    ((Circle)shape).setCenterY(((Circle)shape).getCenterY() + 5);
                }
            }
        }
        if (south) {
            for(Shape shape: shapeList) {
                if (shape instanceof Line) {
                    ((Line)shape).setStartY(((Line)shape).getStartY() - 5);
                    ((Line)shape).setEndY(((Line)shape).getEndY() - 5);
                } else if (shape instanceof Circle) {
                    ((Circle)shape).setCenterY(((Circle)shape).getCenterY() - 5);
                }
            }
        }
        if (east) {
            for(Shape shape: shapeList) {
                if (shape instanceof Line) {
                    ((Line)shape).setStartX(((Line)shape).getStartX() - 5);
                    ((Line)shape).setEndX(((Line)shape).getEndX() - 5);
                } else if (shape instanceof Circle) {
                    ((Circle)shape).setCenterX(((Circle)shape).getCenterX() - 5);
                }
            }
        }
        if (west) {
            for(Shape shape: shapeList) {
                if (shape instanceof Line) {
                    ((Line)shape).setStartX(((Line)shape).getStartX() + 5);
                    ((Line)shape).setEndX(((Line)shape).getEndX() + 5);
                } else if (shape instanceof Circle) {
                    ((Circle)shape).setCenterX(((Circle)shape).getCenterX() + 5);
                }
            }
        }
    }

    private void centerMap() {
        int i = 0;
        for(Shape shape: shapeList) {
            double[] startShape = startShapes.get(i);
            if (shape instanceof Line) {
                ((Line)shape).setStartX(startShape[0]);
                ((Line)shape).setEndX(startShape[1]);
                ((Line)shape).setStartY(startShape[2]);
                ((Line)shape).setEndY(startShape[3]);
            } else if (shape instanceof Circle) {
                ((Circle)shape).setCenterY(startShape[0]);
                ((Circle)shape).setCenterX(startShape[1]);
            }
            i++;
        }
    }


    /**
     * draw
     * draws shapes on scene
     * @param root group that contains shapes
     */
    private void draw(Group root) {
        for (Body body: bodyList) {
            float x = toPixelX(body.getPosition().x);
            float y = toPixelY(body.getPosition().y);
            Fixture fixture = body.getFixtureList();
            if (fixture.getType() == ShapeType.POLYGON) {
                PolygonShape shape = (PolygonShape)fixture.getShape();
                for (int i = 0; i < shape.getVertexCount() - 1; i++) {
                    Line line = new Line();
                    float x0 = toPixelX(shape.getVertex(i).x) + x + 200f;
                    float y0 = toPixelY(shape.getVertex(i).y) + y - 900f;
                    float x1 = toPixelX(shape.getVertex(i + 1).x) + x + 200f;
                    float y1 = toPixelY(shape.getVertex(i + 1).y) + y - 900f;
                    line.setStartX(x0);
                    line.setStartY(y0);
                    line.setEndX(x1);
                    line.setEndY(y1);
                    shapeList.add(line);
                    root.getChildren().add(line);
                }
                Line line = new Line();
                float x0 = toPixelX(shape.getVertex(shape.getVertexCount() - 1).x) + x + 200f;
                float y0 = toPixelY(shape.getVertex(shape.getVertexCount() - 1).y) + y - 900f;
                float x1 = toPixelX(shape.getVertex(0).x) + x + 200f;
                float y1 = toPixelY(shape.getVertex(0).y) + y - 900f;
                line.setStartX(x0);
                line.setStartY(y0);
                line.setEndX(x1);
                line.setEndY(y1);
                shapeList.add(line);
                root.getChildren().add(line);
            } else if (fixture.getType() == ShapeType.CIRCLE) {
                Circle circle = new Circle();
                CircleShape shape = (CircleShape)fixture.getShape();
                circle.setRadius(shape.getRadius());
                circle.setCenterX(x);
                circle.setCenterY(y);
                shapeList.add(circle);
                root.getChildren().add(circle);
            }
        }
    }

    private void createBodyList() {
        bodyList = new Body[world.getBodyCount()];
        Body body = world.getBodyList();
        for (int i = 0; i < world.getBodyCount(); i++) {
            bodyList[i] = body;
            body = body.getNext();
        }
    }

    private void getStartPos() {
        for (Shape shape: shapeList) {
            double[] vertices = new double[4];
            if (shape instanceof Line) {
                vertices[0] = ((Line)shape).getStartX();
                vertices[1] = ((Line)shape).getEndX();
                vertices[2] = ((Line)shape).getStartY();
                vertices[3] = ((Line)shape).getEndY();
            } else if (shape instanceof Circle) {
                vertices[0] = ((Circle)shape).getCenterX();
                vertices[1] = ((Circle)shape).getCenterY();
            }
            startShapes.add(vertices);
        }
    }

    /**
     * toPixelX
     * converts meters to pixels for x-values
     * @param x x-value in meters
     * @return x-value in pixels
     */
    private static float toPixelX(float x) {
        return x * 50f;
    }

    /**
     * toPixelY
     * converts meters to pixels for y-values
     * @param y y-value in meters
     * @return y-value in pixels
     */
    private static float toPixelY(float y) {
        return HEIGHT - y * 50f;
    }

    public float[][] rouletteSelection(ArrayList<Car> currentGen){

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
                if (!currentGen.get(0).getSelected()) {
                    parents.add(currentGen.get(0));
                    currentGen.get(0).setSelected();
                }
            }
            for (int j = 1; j < rouletteWheel.length; j++){
                if ((selectionNum > rouletteWheel[j-1]) && (selectionNum <= rouletteWheel[j])){
                    if (!currentGen.get(j).getSelected()) {
                        parents.add(currentGen.get(1));
                        currentGen.get(j).setSelected();
                    }
                }
            }
        }while (parents.size() < 10);

        //Call Crossover method
        //next step
        float[][] childrenGenomes = crossover(parents);
        return childrenGenomes;
    }

    public float [][] crossover (ArrayList<Car> parents){
        float[][] children = new float[20][22];
        int i = 0;
        for (int two = 0; two < 2; two++) {
            for (int j = 0; j < parents.size(); j++) {
                Car temp = parents.get(j);
                int random = (int) (Math.random() * parents.size());
                parents.set(j, parents.get(random));
                parents.set(random, temp);
            }
            for (int j = 0; j < parents.size(); j += 2) {
                Car parent0 = parents.get(j);
                Car parent1 = parents.get(j + 1);
                int point0 = ((int) (Math.random() * 11) + 1) * 2 - 1;
                int point1;
                do {
                    point1 = ((int) (Math.random() * 11) + 1) * 2 - 1;
                } while (point0 == point1);
                if (point0 > point1) {
                    int temp = point0;
                    point0 = point1;
                    point1 = temp;
                }
                float[] genome0 = new float[22];
                float[] genome1 = new float[22];
                for (int k = 0; k < point0 - 1; k++) {
                    genome0[k] = parent0.getGenome()[k];
                    genome1[k] = parent1.getGenome()[k];
                }
                for (int k = point0 - 1; k < point1 - 1; k++) {
                    genome0[k] = parent1.getGenome()[k];
                    genome1[k] = parent0.getGenome()[k];
                }
                for (int k = point1 - 1; k < genome0.length; k++) {
                    genome0[k] = parent0.getGenome()[k];
                    genome1[k] = parent1.getGenome()[k];
                }
                children[i] = genome0;
                children[i + 1] = genome1;
                i += 2;
            }
        }
        float[][] childrenGenomes = mutation(children);
        return childrenGenomes;
    }

    public float[][] mutation(float[][] children){
        final double MUTATION_RATE = 0.2;
        final double MUTATION_EFFECT = 0.2;
        for (int i = 0; i < children.length; i++) {
            for (int j = 0; j < children[i].length; j++) {
                double random = Math.random();
                if (random <= MUTATION_RATE) {
                    float mutation = (float) (Math.random() * MUTATION_EFFECT * 2 - MUTATION_EFFECT);
                    children[i][j] += mutation;
                }
            }
        }
        return children;
    }

    public ArrayList<Car> createNextGen(float[][] childrenGenomes){
        ArrayList<Car> nextGen = new ArrayList<>();

        for (int i = 0; i < childrenGenomes.length; i++){
            nextGen.add (new Car(childrenGenomes[i], world));
        }

        return nextGen;
    }


    public static void main(String[] args) {
        launch(args);
    }

}
