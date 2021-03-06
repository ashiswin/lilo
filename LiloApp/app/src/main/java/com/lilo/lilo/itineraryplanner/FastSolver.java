package com.lilo.lilo.itineraryplanner;

import java.util.*;

/**
 * Created by Oon Tong on 11/27/2017.
 */

public class FastSolver {
    Location start;
    List<Location> destinations;
    double max;

    double currentPrice;
    double currentTime = Integer.MAX_VALUE;
    /*Stack<Path> workingStack = new Stack<>();*/
    Stack<double[]> workingStack = new Stack<>();
    Stack<Integer> visited = new Stack<>();
    /*List<Path> solution;*/
    public List<double[]> solution = new LinkedList<>();
    public FastSolver(Location start, List<Location> destinations, double max) {
        this.start = start;
        this.destinations = destinations;
        this.max = max;
        currentPrice = max+1;
        Collections.sort(start.edges,new Comparator<double[]>() {
            @Override
            public int compare(double[] ints, double[] t1) {
                if (ints[2] > t1[2]){
                    return 1;
                } else if (ints[2] < t1[2]){
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (Location i : destinations) {
            Collections.sort(i.edges,new Comparator<double[]>() {
                @Override
                public int compare(double[] ints, double[] t1) {
                    if (ints[2] > t1[2]){
                        return 1;
                    } else if (ints[2] < t1[2]){
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }


    /* PATH = int[4]
    * int0 = METHOD
    *           1 = walking
    *           2 = bussing
    *           3 = taxing
    *
    *int1 = TO WHICH LOCATION (ID)
    * int2 = time
    * int3 = price*/
    public void run(Location i) {

        visited.push(i.locationID);
        if (visited.size() == destinations.size() + 1) {
            for (double[] backtoStart : i.edges) {
                if (backtoStart[1] == start.locationID) {
                    workingStack.push(backtoStart);
                    double[] timePrice = getTimePrice(workingStack);
                    if (currentTime > timePrice[0] && max >= timePrice[1]) {
                        solution = new LinkedList<>(workingStack);
                        currentPrice = timePrice[1];
                        currentTime = timePrice[0];
                        for (double[] qwe: solution){
                            System.out.printf(Arrays.toString(qwe) + " ");
                        }
                        System.out.println("\n");
                    }
                    workingStack.pop();
                }
            }
            visited.pop();

            return;
        }


            /*double[] timePrice = getTimePrice();
            if (currentTime > timePrice[0] && max > currentPrice){
                solution = new LinkedList<>(workingStack);

            }*/



        for (double[] path : i.edges) {
            if (!visited.contains((int)path[1])) {
                workingStack.push(path);
                for (Location e : destinations){
                    if (e.locationID == path[1]){
                        run(e);
                    }
                }
                workingStack.pop();
                if (!solution.isEmpty()){
                    return;
                }
            }
        }

        visited.pop();

    }




    public double[] getTimePrice(List<double[]> l){
        double[] timePrice = new double[2];

        for (double[] e : l){
            timePrice[0] += e[2];
            timePrice[1] += e[3];
        }

        return timePrice;
    }


   /* public static void main(String[] args) {
        Location start = new Location("MBS",0);
        Location one = new Location("CV",1);
        Location two = new Location("China",2);
        Location three = new Location("TWD",3);
        Location four = new Location("ASD",4);

        for (int i = 0; i < 5; i++){
            if (i == 0){
                continue;
            }
            Random randomizer = new Random();
            for (int j = 1;j < 4;j++){
                start.edges.add(new int[]{j,i,randomizer.nextInt(20)+1,randomizer.nextInt(20)+1});
            }
        }


        for (int i = 0; i < 5; i++){
            if (i == 1){
                continue;
            }
            Random randomizer = new Random();
            for (int j = 1;j < 4;j++){
                one.edges.add(new int[]{j,i,randomizer.nextInt(20)+1,randomizer.nextInt(20)+1});
            }
        }

        for (int i = 0; i < 5; i++){
            if (i == 2){
                continue;
            }
            Random randomizer = new Random();
            for (int j = 1;j < 4;j++){
                two.edges.add(new int[]{j,i,randomizer.nextInt(20)+1,randomizer.nextInt(20)+1});
            }
        }

        for (int i = 0; i < 5; i++){
            if (i == 3){
                continue;
            }
            Random randomizer = new Random();
            for (int j = 1;j < 4;j++){
                three.edges.add(new int[]{j,i,randomizer.nextInt(20)+1,randomizer.nextInt(20)+1});
            }
        }

        for (int i = 0; i < 5; i++){
            if (i == 4){
                continue;
            }
            Random randomizer = new Random();
            for (int j = 1;j < 4;j++){
                four.edges.add(new int[]{j,i,randomizer.nextInt(20)+1,randomizer.nextInt(20)+1});
            }
        }

        List<Location> destinations = new LinkedList<>();
        destinations.add(one);
        destinations.add(two);
        destinations.add(three);
        destinations.add(four);

        double it = System.currentTimeMillis();
        FastSolver b = new FastSolver(start,destinations,30);
        BruteSolver x = new BruteSolver(start,destinations,30);
        x.run(start);


        b.run(start);

        System.out.println("SPEED =" + (System.currentTimeMillis() - it));

        for (int[] e : b.solution){
            System.out.println("MODE =" + e[0]);
            System.out.printf("" + e[2] + " ");
            System.out.printf("" + e[3] + " ");
            System.out.printf("ID =" +e[1]);
            System.out.println();
        }

        System.out.println();
        System.out.println("TIME " + b.getTimePrice(b.solution)[0]);
        System.out.println("PRICE " + b.getTimePrice(b.solution)[1]);
        System.out.println();
        System.out.println("TIME " + x.getTimePrice(x.solution)[0]);
        System.out.println("PRICE " +x.getTimePrice(x.solution)[1]);

        System.out.println("DIFFERENCE " + ((b.getTimePrice(b.solution)[0] - x.getTimePrice(x.solution)[0])/x.getTimePrice(x.solution)[0]));

    }*/
}


