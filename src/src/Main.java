import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ConfigLoader loader = new ConfigLoader();
        try {
            // Aqui a gente ta usando o que vem junto sem importar por parametro.
            loader.load("src/src/test.yml");
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            return;
        }

        System.out.println("#### ESSE SIMULADOR FOI FEITO POR: ANDRÉ MACHADO, JOÃO FILIPE, RICARDO BATISTA E LUANA ####");

        List<Queue> queues = new ArrayList<>(loader.queues.values());
        Generator generator;
        if (loader.usePredefinedRandoms) {
            generator = new Generator(loader.rndNumbers);
        } else {
            generator = new Generator(loader.seed, loader.randoms);
        }
        Simulator simulator = new Simulator(queues, generator);

        
        for (ConfigLoader.InitialArrival ia : loader.initialArrivals) {
            Queue q = loader.queues.get(ia.queueName);
            if (q != null) {
                simulator.schedule(new Event(Event.Type.ARRIVAL, ia.time, q));
            }
        }

        simulator.simulate();
    }
}
