import java.util.List;
import java.util.PriorityQueue;

public class Simulator {
    private List<Queue> queues;
    private PriorityQueue<Event> scheduler;
    private Generator generator;
    private double globalTime = 0.0;
    private double lastEventTime = 0.0;

    public Simulator(List<Queue> queues, Generator generator) {
        this.queues = queues;
        this.generator = generator;
        this.scheduler = new PriorityQueue<>();
    }

    public void schedule(Event e) {
        scheduler.add(e);
    }

    private void updateTimes(double currentTime) {
        double delta = currentTime - lastEventTime;
        for (Queue q : queues) {
            q.accumulateTime(q.getCurrentState(), delta);
        }
        lastEventTime = currentTime;
    }

    public void simulate() {
        while (!scheduler.isEmpty()) {
            Event e = scheduler.poll();
            globalTime = e.getTime();
            updateTimes(globalTime);

            if (e.getType() == Event.Type.ARRIVAL) {
                processArrival(e.getSourceQueue());
            } else if (e.getType() == Event.Type.SERVICE_END) {
                processServiceEnd(e.getSourceQueue());
            }

            if (!generator.hasRandom()) break;
        }
        report();
    }

    private void processArrival(Queue q) {

        if (q.getCurrentState() < q.getCapacity()) {
            q.setCurrentState(q.getCurrentState() + 1);
            if (q.getCurrentState() <= q.getServers()) {
                scheduleService(q);
            }
        } else {
            q.incrementLosses();
        }

        if (q.getArrivalMin() > 0 || q.getArrivalMax() > 0) {
            double rnd = generator.nextRandom();
            if (rnd != -1) {
                double nextArrivalTime = q.getArrivalMin() + (q.getArrivalMax() - q.getArrivalMin()) * rnd;
                scheduler.add(new Event(Event.Type.ARRIVAL, globalTime + nextArrivalTime, q));
            }
        }
    }

    private void scheduleService(Queue q) {
        double rnd = generator.nextRandom();
        if (rnd != -1) {
            double serviceTime = q.getServiceMin() + (q.getServiceMax() - q.getServiceMin()) * rnd;
            scheduler.add(new Event(Event.Type.SERVICE_END, globalTime + serviceTime, q));
        }
    }

    private void processServiceEnd(Queue q) {
        q.setCurrentState(q.getCurrentState() - 1);

        if (!q.getRoutes().isEmpty()) {
            double rnd = generator.nextRandom();
            if (rnd != -1) {
                double sum = 0;
                for (Queue.Route route : q.getRoutes()) {
                    sum += route.probability;
                    if (rnd <= sum) {
                        handleInternalArrival(route.destination);
                        break;
                    }
                }
            }
        }

        if (q.getCurrentState() >= q.getServers()) {
            scheduleService(q);
        }
    }

    private void handleInternalArrival(Queue q) {
        if (q.getCurrentState() < q.getCapacity()) {
            q.setCurrentState(q.getCurrentState() + 1);
            if (q.getCurrentState() <= q.getServers()) {
                scheduleService(q);
            }
        } else {
            q.incrementLosses();
        }
    }

    private void report() {
        System.out.println("Simulation Report");
        System.out.println("Global Time: " + String.format("%.4f", globalTime));
        for (Queue q : queues) {
            System.out.println("\nQueue: " + q.getName());
            System.out.println("Capacity: " + q.getCapacity() + ", Servers: " + q.getServers());
            System.out.println("Losses: " + q.getLosses());
            System.out.printf("%-10s | %-15s | %-12s\n", "State", "Accumulated Time", "Probability");
            for (int i = 0; i < q.getAccumulatedTimes().length; i++) {
                double time = q.getAccumulatedTimes()[i];
                if (time > 0 || i <= q.getCurrentState()) {
                    double prob = (time / globalTime) * 100;
                    System.out.printf("%-10d | %-15.4f | %.2f%%\n", i, time, prob);
                }
            }
        }
    }
}
