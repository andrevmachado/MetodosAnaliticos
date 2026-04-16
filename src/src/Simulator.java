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
            } else if (e.getType() == Event.Type.DEPARTURE) {
                processDeparture(e.getSourceQueue());
            } else if (e.getType() == Event.Type.TRANSFER) {
                processTransfer(e.getSourceQueue(), e.getDestinationQueue());
            }

            if (!generator.hasRandom()) break;
        }
        report();
    }

    private void processArrival(Queue q) {
        if (q.getCurrentState() < q.getCapacity()) {
            q.setCurrentState(q.getCurrentState() + 1);
            if (q.getCurrentState() <= q.getServers()) {
                double rnd = generator.nextRandom();
                if (rnd != -1) {
                    double serviceTime = q.getServiceMin() + (q.getServiceMax() - q.getServiceMin()) * rnd;
                    scheduleDepartureOrTransferEvent(q, globalTime + serviceTime);
                }
            }
        } else {
            q.incrementLosses();
        }

        // Schedule next external arrival if this queue has external arrivals
        if (q.getArrivalMin() > 0 || q.getArrivalMax() > 0) {
            double rnd = generator.nextRandom();
            if (rnd != -1) {
                double nextArrivalTime = q.getArrivalMin() + (q.getArrivalMax() - q.getArrivalMin()) * rnd;
                scheduler.add(new Event(Event.Type.ARRIVAL, globalTime + nextArrivalTime, q));
            }
        }
    }

    private void processDeparture(Queue q) {
        q.setCurrentState(q.getCurrentState() - 1);
        if (q.getCurrentState() >= q.getServers()) {
            double rnd = generator.nextRandom();
            if (rnd != -1) {
                double serviceTime = q.getServiceMin() + (q.getServiceMax() - q.getServiceMin()) * rnd;
                scheduleDepartureOrTransferEvent(q, globalTime + serviceTime);
            }
        }
    }

    private void processTransfer(Queue source, Queue destination) {
        source.setCurrentState(source.getCurrentState() - 1);
        if (source.getCurrentState() >= source.getServers()) {
            double rnd = generator.nextRandom();
            if (rnd != -1) {
                double serviceTime = source.getServiceMin() + (source.getServiceMax() - source.getServiceMin()) * rnd;
                scheduleDepartureOrTransferEvent(source, globalTime + serviceTime);
            }
        }

        if (destination.getCurrentState() < destination.getCapacity()) {
            destination.setCurrentState(destination.getCurrentState() + 1);
            if (destination.getCurrentState() <= destination.getServers()) {
                double rnd = generator.nextRandom();
                if (rnd != -1) {
                    double serviceTime = destination.getServiceMin() + (destination.getServiceMax() - destination.getServiceMin()) * rnd;
                    scheduleDepartureOrTransferEvent(destination, globalTime + serviceTime);
                }
            }
        } else {
            destination.incrementLosses();
        }
    }

    private void scheduleDepartureOrTransferEvent(Queue q, double time) {
        if (q.getRoutes().isEmpty()) {
            scheduler.add(new Event(Event.Type.DEPARTURE, time, q));
        } else {
            if (q.getRoutes().size() == 1 && q.getRoutes().get(0).probability == 1.0) {
                scheduler.add(new Event(Event.Type.TRANSFER, time, q, q.getRoutes().get(0).destination));
            } else {
                double rnd = generator.nextRandom();
                if (rnd != -1) {
                    double sum = 0;
                    for (Queue.Route route : q.getRoutes()) {
                        sum += route.probability;
                        if (rnd <= sum) {
                            scheduler.add(new Event(Event.Type.TRANSFER, time, q, route.destination));
                            return;
                        }
                    }
                    scheduler.add(new Event(Event.Type.DEPARTURE, time, q));
                }
            }
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
                double prob = (time / globalTime) * 100;
                System.out.printf("%-10d | %-15.4f | %.2f%%\n", i, time, prob);
            }
        }
    }
}
