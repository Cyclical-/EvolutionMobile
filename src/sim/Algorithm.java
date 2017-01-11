package sim;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by A on 1/9/2017.
 */


public class Algorithm {
    int population;
    double mutationRate;
    ArrayList<Car> parents;
    ArrayList<Car> nextGen;

    Algorithm(int population, double mutationRate){
        this.population = population;
        this.mutationRate = mutationRate;
    }

    public void rouletteSelection(ArrayList<Car> currentGen){


    }

    public void crossover (){

    }

    public void mutation(){

    }

    public void createChild(){
        new Car(genome)
    }

    public ArrayList<Car> getNextGen(){

    }

}
