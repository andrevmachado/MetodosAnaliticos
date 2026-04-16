import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        // Queue 1 - G/G/2/3
        Queue q1 = new Queue("Queue 1", 3, 2, 1.0, 4.0, 3.0, 4.0);

        // Queue 2 - G/G/1/5
        Queue q2 = new Queue("Queue 2", 5, 1, 0.0, 0.0, 2.0, 3.0);

        q1.addRoute(q2, 1.0);

        List<Queue> queues = new ArrayList<>();
        queues.add(q1);
        queues.add(q2);

        Generator generator = new Generator(12345, 100000);

        Simulator simulator = new Simulator(queues, generator);

        simulator.schedule(new Event(Event.Type.ARRIVAL, 1.5, q1));

        simulator.simulate();
    }
}
