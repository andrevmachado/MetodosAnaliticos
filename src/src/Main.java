import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // G/G/1/5
        SimuladorFila sim1 = new SimuladorFila(5, 1, 2.0, 5.0, 3.0, 5.0, 2.0);
        sim1.simular();

        // G/G/2/5
        SimuladorFila sim2 = new SimuladorFila(5, 2, 2.0, 5.0, 3.0, 5.0, 2.0);
        sim2.simular();
    }
}