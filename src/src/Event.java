public class Event implements Comparable<Event> {
    public enum Type {
        ARRIVAL, DEPARTURE, TRANSFER
    }

    private Type type;
    private double time;
    private Queue sourceQueue;
    private Queue destinationQueue;

    public Event(Type type, double time, Queue sourceQueue) {
        this.type = type;
        this.time = time;
        this.sourceQueue = sourceQueue;
    }

    public Event(Type type, double time, Queue sourceQueue, Queue destinationQueue) {
        this.type = type;
        this.time = time;
        this.sourceQueue = sourceQueue;
        this.destinationQueue = destinationQueue;
    }

    public Type getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public Queue getSourceQueue() {
        return sourceQueue;
    }

    public Queue getDestinationQueue() {
        return destinationQueue;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }
}
