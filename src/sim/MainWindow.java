package sim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
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

import java.util.ArrayList;

/**
 * MainWindow.java
 * @author Kevin Chik and Anthony Lai
 * 22/12/2016
 */
public class MainWindow extends Application {

    //world
    private static final Vec2 GRAVITY = new Vec2(0.0F, -9.81F);
    private World world = new World(GRAVITY);
    private static final int FPS = 60;

    //stage
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private boolean north, south, east, west;
    private int[] camera = new int[2];

    //algorithm
    private int generation = 0;
    private int carNumber = 0;
    private float[][] genome = new float[20][22];
    private double[] distance = new double[20];
//    private static int deadCars = 0;

    //body list
    private Body[] bodyList;
    private Shape[][][] shapeList;
    private ArrayList<double[]> startShapes = new ArrayList<>();

    /**
     * start
     * creates stage and runs genetic algorithm
     * @author Kevin Chik and Anthony Lai
     * @param primaryStage stage
     */
    @Override
    public void start(Stage primaryStage) {
        //stage settings
        primaryStage.setTitle("EvolutionMobile");
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        //root
        Group root = new Group();

        //ground
        Ground ground = new Ground(world);
        ground.createGround();
        createBodyList();
        createShapeList();
        getStartPos();

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

        runGeneticAlgorithm(root);

    }

    /**
     * runGeneticAlgorithm
     * genetic algorithm
     * @author Kevin Chik and Anthony Lai
     * @param root group that contains all shapes to be displayed
     */
    private void runGeneticAlgorithm(Group root) {
        Car car;
        if (generation > 0) {
            car = new Car(genome[generation], world);
        } else {
            car = new Car(CarDefinition.createRandomCar(), world);
        }
        createBodyList();
        createShapeList();
        drawCar(root);
        drawGround(root);
        //evaluate
        final Timeline timeline = new Timeline();
        try {
            evaluate(timeline, car, root);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * evaluate
     * sets up keyframes for every 1/60s
     * @author Kevin Chik and Anthony Lai
     * @throws InterruptedException if thread is interrupted
     */
    private void evaluate(Timeline timeline, Car car, Group root) throws InterruptedException {
        timeline.setCycleCount(Timeline.INDEFINITE);
        Duration duration = Duration.seconds(1.0 / FPS);
        EventHandler<ActionEvent> actionEvent = terminate -> {
            world.step(1.0f / FPS, 8, 3);
            createBodyList();
            update();
            if (car.checkDeath()) {
                genome[carNumber] = car.getGenome();
                distance[carNumber] = car.getFitnessScore();
                centerMap();
                for (Shape[][] body : shapeList) {
                    if (body.length < 4) {
                        for (Shape[] fixture : body) {
                            for (Shape line : fixture) {
                                root.getChildren().remove(line);
                            }
                        }
                    }
                }
                car.kill();
                timeline.pause();
                carNumber++;
                if (carNumber == 20) {
                    genome = rouletteSelection(genome, distance);
                    generation++;
                    carNumber = 0;
                }
                runGeneticAlgorithm(root);
            }
        };
        KeyFrame keyFrame = new KeyFrame(duration, actionEvent, null, null);
        timeline.getKeyFrames().add(keyFrame);
        timeline.playFromStart();
    }

    /**
     * update
     * updates position of car
     * @author Kevin Chik
     */
    private void update() {
        camera();
        for (int i = 0; i < shapeList.length; i++) {
            float x = Util.toPixelX(bodyList[i].getPosition().x) + camera[1] + 200f;
            float y = Util.toPixelY(bodyList[i].getPosition().y) + camera[0] - 900f;
            float angle = -(bodyList[i].getTransform().q.getAngle());
            Fixture fixture = bodyList[i].getFixtureList();
            for (int j = 0; j < shapeList[i].length; j++) {
                if (fixture.getType() == ShapeType.POLYGON) {
                    PolygonShape shape = (PolygonShape)fixture.getShape();
                    int k = 0;
                    for (; k < shapeList[i][j].length - 1; k++) {
                        Line line = ((Line)shapeList[i][j][k]);
                        float x0 = shape.getVertex(k).x;
                        float y0 = shape.getVertex(k).y;
                        float ax0 = (float) (Math.sin(angle) * y0 + Math.cos(angle) * x0);
                        float ay0 = (float) (Math.cos(angle) * y0 - Math.sin(angle) * x0);
                        float x1 = shape.getVertex(k + 1).x;
                        float y1 = shape.getVertex(k + 1).y;
                        float ax1 = (float) (Math.sin(angle) * y1 + Math.cos(angle) * x1);
                        float ay1 = (float) (Math.cos(angle) * y1 - Math.sin(angle) * x1);
                        line.setStartX(Util.toPixelX(ax0) + x);
                        line.setStartY(Util.toPixelY(ay0) + y);
                        line.setEndX(Util.toPixelX(ax1) + x);
                        line.setEndY(Util.toPixelY(ay1) + y);
                        shapeList[i][j][k] = line;
                    }
                    Line line = ((Line)shapeList[i][j][k]);
                    float x0 = shape.getVertex(0).x;
                    float y0 = shape.getVertex(0).y;
                    float ax0 = (float) (Math.sin(angle) * y0 + Math.cos(angle) * x0);
                    float ay0 = (float) (Math.cos(angle) * y0 - Math.sin(angle) * x0);
                    float x1 = shape.getVertex(k).x;
                    float y1 = shape.getVertex(k).y;
                    float ax1 = (float) (Math.sin(angle) * y1 + Math.cos(angle) * x1);
                    float ay1 = (float) (Math.cos(angle) * y1 - Math.sin(angle) * x1);
                    line.setStartX(Util.toPixelX(ax0) + x);
                    line.setStartY(Util.toPixelY(ay0) + y);
                    line.setEndX(Util.toPixelX(ax1) + x);
                    line.setEndY(Util.toPixelY(ay1) + y);
                    shapeList[i][j][k] = line;
                } else if (fixture.getType() == ShapeType.CIRCLE) {
                    CircleShape shape = (CircleShape)fixture.getShape();
                    Circle circle = ((Circle)shapeList[i][j][0]);
                    circle.setFill(Color.TRANSPARENT);
                    circle.setStroke(Color.BLACK);
                    circle.setRadius(shape.getRadius() * 50f);
//                    float ax = (float) (Math.sin(angle) * y + Math.cos(angle) * x);
//                    float ay = (float) (Math.cos(angle) * y - Math.sin(angle) * x);
                    circle.setCenterX(x);
                    circle.setCenterY(y + 600f);
                    shapeList[i][j][0] = circle;
                }
                fixture = fixture.getNext();
            }
        }

    }

    /**
     * camera
     * moves camera to follow car or arrow keys
     * @author Kevin Chik
     */
    private void camera() {
        if (north) {
            camera[0] += 5;
        }
        if (south) {
            camera[0] -= 5;
        }
        if (east) {
            camera[1] -= 5;
        }
        if (west) {
            camera[1] += 5;
        }
    }

    /**
     * draw
     * draws ground on scene
     * @author Kevin Chik
     * @param root group that contains all shapes to be displayed
     */
    private void drawGround(Group root) {
        for (Shape[][] body : shapeList) {
            for (Shape[] fixture : body) {
                if (fixture.length == 4) {
                    for (Shape line : fixture) {
                        root.getChildren().add(line);
                    }
                }
            }
        }
    }

    /**
     * drawCar
     * draws car on scene
     * @author Kevin Chik
     * @param root group that contains all shapes to be displayed
     */
    private void drawCar(Group root) {
        for (Shape[][] body : shapeList) {
            for (Shape[] fixture : body) {
                if (fixture.length < 4) {
                    for (Shape line : fixture) {
                        root.getChildren().add(line);
                    }
                }
            }
        }
    }

    /**
     * createBodyList
     * puts all JBox2d bodies in an array
     * @author Kevin Chik
     */
    private void createBodyList() {
        bodyList = new Body[world.getBodyCount()];
        Body body = world.getBodyList();
        for (int i = 0; i < world.getBodyCount(); i++) {
            bodyList[i] = body;
            body = body.getNext();
        }
    }

    /**
     * createShapeList
     * converts bodyList to JavaFX shapes
     * @author Kevin Chik
     */
    private void createShapeList() {
        shapeList = new Shape[bodyList.length][][];
        for (int i = 0; i < world.getBodyCount(); i++) {
            int fixtureCount = 0;
            Fixture fixture = bodyList[i].getFixtureList();
            do {
                fixtureCount++;
                fixture = fixture.getNext();
            } while (fixture != null);
            shapeList[i] = new Shape[fixtureCount][];
            fixture = bodyList[i].getFixtureList();
            for (int j = 0; j < fixtureCount; j++) {
                if (fixture.getType() == ShapeType.POLYGON) {
                    int vertexCount = ((PolygonShape)fixture.getShape()).getVertexCount();
                    shapeList[i][j] = new Line[vertexCount];
                    int k = 0;
                    for (; k < vertexCount - 1; k++) {
                        shapeList[i][j][k] = new Line();
                    }
                    shapeList[i][j][k] = new Line();
                } else if (fixture.getType() == ShapeType.CIRCLE) {
                    shapeList[i][j] = new Circle[1];
                    shapeList[i][j][0] = new Circle();
                }
                fixture = fixture.getNext();
            }
        }
    }

    /**
     * centerMap
     * brings camera to start of map
     * @author Kevin Chik
     */
    private void centerMap() {
        int i = 0;
        for(Shape[][] shape: shapeList) {
            for (Shape[] fixture : shape) {
                double[] startShape = startShapes.get(i);
                for (Shape line : fixture) {
                    if (line instanceof Line) {
                        ((Line) line).setStartX(startShape[0]);
                        ((Line) line).setEndX(startShape[1]);
                        ((Line) line).setStartY(startShape[2]);
                        ((Line) line).setEndY(startShape[3]);
                    } else if (line instanceof Circle) {
                        ((Circle) line).setCenterY(startShape[0]);
                        ((Circle) line).setCenterX(startShape[1]);
                    }
                }
                i++;
            }
        }
    }

    /**
     * getStartPos
     * gets start positions of ground tiles
     * @author Kevin Chik
     */
    private void getStartPos() {
        for (Shape[][] shape: shapeList) {
            for (Shape[] fixture : shape) {
                for (Shape line : fixture) {
                    double[] vertices = new double[4];
                    if (line instanceof Line) {
                        vertices[0] = ((Line) line).getStartX();
                        vertices[1] = ((Line) line).getEndX();
                        vertices[2] = ((Line) line).getStartY();
                        vertices[3] = ((Line) line).getEndY();
                    } else if (line instanceof Circle) {
                        vertices[0] = ((Circle) line).getCenterX();
                        vertices[1] = ((Circle) line).getCenterY();
                    }
                    startShapes.add(vertices);
                }
            }
        }
    }

    /**
     * rouletteSelection
     * determines parents for next generation
     * @author Anthony Lai
     * @param currentGen current generation of cars
     * @param distance fitness scores
     * @return parents for next generation
     */
    private float[][] rouletteSelection(float[][] currentGen, double[] distance){

        //fitnessScores - index 0 is the car's fitness score - index 1 is the car's probability of selection
        double [][] fitnessScores = new double[20][2];
        for (int i = 0; i < fitnessScores.length; i++) {
            fitnessScores[i][0] = distance[i];
        }

        //Find sum of all fitness scores
        double sumOfFitnessScores = 0;
        for (double[] fitnessScore : fitnessScores) {
            sumOfFitnessScores = fitnessScore[0] + sumOfFitnessScores;
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
        ArrayList<float[]> parents = new ArrayList<>();
        boolean[] selected = new boolean[20];
        do{
            selectionNum = (Math.random()*101);

            if ((selectionNum >= 0) && (selectionNum <= rouletteWheel[0])){
                if (!selected[0]) {
                    parents.add(currentGen[0]);
                    selected[0] = true;
                }
            }
            for (int j = 1; j < rouletteWheel.length; j++){
                if ((selectionNum > rouletteWheel[j-1]) && (selectionNum <= rouletteWheel[j])){
                    if (!selected[j]) {
                        parents.add(currentGen[1]);
                        selected[j] = true;
                    }
                }
            }
        }while (parents.size() < 10);

        //Call Crossover method
        //next step
        return crossover(parents);
    }

    /**
     * crossover
     * performs crossover to create child generation
     * @author Kevin Chik
     * @param parents parent generation
     * @return child generation
     */
    private float [][] crossover (ArrayList<float[]> parents){
        float[][] children = new float[20][22];
        int i = 0;
        for (int two = 0; two < 2; two++) {
            for (int j = 0; j < parents.size(); j++) {
                float[] temp = parents.get(j);
                int random = (int) (Math.random() * parents.size());
                parents.set(j, parents.get(random));
                parents.set(random, temp);
            }
            for (int j = 0; j < parents.size(); j += 2) {
                float[] parent0 = parents.get(j);
                float[] parent1 = parents.get(j + 1);
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
                    genome0[k] = parent0[k];
                    genome1[k] = parent1[k];
                }
                for (int k = point0 - 1; k < point1 - 1; k++) {
                    genome0[k] = parent1[k];
                    genome1[k] = parent0[k];
                }
                for (int k = point1 - 1; k < genome0.length; k++) {
                    genome0[k] = parent0[k];
                    genome1[k] = parent1[k];
                }
                children[i] = genome0;
                children[i + 1] = genome1;
                i += 2;
            }
        }

        return mutation(children);
    }

    /**
     * mutation
     * mutates children
     * @author Kevin Chik
     * @param children child generation
     * @return mutated child generation
     */
    private float[][] mutation(float[][] children){
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

    public static void main(String[] args) {
        launch(args);
    }

}
