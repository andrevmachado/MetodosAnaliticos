import java.util.ArrayList;
import java.util.List;

public class Queue {
    private String name;
    private int capacity;
    private int servers;
    private double arrivalMin, arrivalMax;
    private double serviceMin, serviceMax;

    private int currentState = 0;
    private int losses = 0;
    private double[] accumulatedTimes;

    private List<Route> routes = new ArrayList<>();

    public Queue(String name, int capacity, int servers, double arrivalMin, double arrivalMax, double serviceMin, double serviceMax) {
        this.name = name;
        this.capacity = capacity;
        this.servers = servers;
        this.arrivalMin = arrivalMin;
        this.arrivalMax = arrivalMax;
        this.serviceMin = serviceMin;
        this.serviceMax = serviceMax;
        this.accumulatedTimes = new double[capacity + 1];
    }

    public void addRoute(Queue destination, double probability) {
        routes.add(new Route(destination, probability));
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public int getServers() { return servers; }
    public double getArrivalMin() { return arrivalMin; }
    public double getArrivalMax() { return arrivalMax; }
    public double getServiceMin() { return serviceMin; }
    public double getServiceMax() { return serviceMax; }
    public int getCurrentState() { return currentState; }
    public void setCurrentState(int currentState) { this.currentState = currentState; }
    public int getLosses() { return losses; }
    public void incrementLosses() { this.losses++; }
    public double[] getAccumulatedTimes() { return accumulatedTimes; }

    public void accumulateTime(int state, double delta) {
        if (state < accumulatedTimes.length) {
            accumulatedTimes[state] += delta;
        }
    }

    public static class Route {
        Queue destination;
        double probability;

        public Route(Queue destination, double probability) {
            this.destination = destination;
            this.probability = probability;
        }
    }
}
