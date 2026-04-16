import java.util.PriorityQueue;

public class SimuladorFila {

    private int capacidade;
    private int servidores;
    private double chegadaMin, chegadaMax;
    private double servicoMin, servicoMax;

    private double tempoGlobal = 0.0;
    private double tempoUltimoEvento = 0.0;
    private int filaAtual = 0;
    private int clientesPerdidos = 0;
    private double[] temposAcumulados;

    private PriorityQueue<Evento> agenda;
    private Gerador gerador;

    public SimuladorFila(int capacidade, int servidores,
                         double chMin, double chMax,
                         double seMin, double seMax,
                         double primeiraChegada) {

        this.capacidade = capacidade;
        this.servidores = servidores;
        this.chegadaMin = chMin;
        this.chegadaMax = chMax;
        this.servicoMin = seMin;
        this.servicoMax = seMax;

        this.temposAcumulados = new double[capacidade + 1];
        this.agenda = new PriorityQueue<>();
        this.gerador = new Gerador();

        agenda.add(new Evento(primeiraChegada, Evento.CHEGADA));
    }

    private void atualizarEstatisticas(double tempoAtual) {
        double delta = tempoAtual - tempoUltimoEvento;
        temposAcumulados[filaAtual] += delta;
        tempoUltimoEvento = tempoAtual;
    }

    public void simular() {
        while (gerador.hasRandom() && !agenda.isEmpty()) {

            Evento evento = agenda.poll();
            tempoGlobal = evento.tempo;

            atualizarEstatisticas(tempoGlobal);

            if (evento.tipo == Evento.CHEGADA) {
                processarChegada();
            } else {
                processarSaida();
            }
        }

        relatorio();
    }

    private void processarChegada() {
        if (filaAtual < capacidade) {
            filaAtual++;

            if (filaAtual <= servidores) {
                double rnd = gerador.nextRandom();
                if (rnd != -1) {
                    double tempoServico = servicoMin + (servicoMax - servicoMin) * rnd;
                    agenda.add(new Evento(tempoGlobal + tempoServico, Evento.SAIDA));
                }
            }
        } else {
            clientesPerdidos++;
        }

        double rnd = gerador.nextRandom();
        if (rnd != -1) {
            double tempoChegada = chegadaMin + (chegadaMax - chegadaMin) * rnd;
            agenda.add(new Evento(tempoGlobal + tempoChegada, Evento.CHEGADA));
        }
    }

    private void processarSaida() {
        filaAtual--;

        if (filaAtual >= servidores) {
            double rnd = gerador.nextRandom();
            if (rnd != -1) {
                double tempoServico = servicoMin + (servicoMax - servicoMin) * rnd;
                agenda.add(new Evento(tempoGlobal + tempoServico, Evento.SAIDA));
            }
        }
    }

    private void relatorio() {
        System.out.println("\n-------------------------------------------");
        System.out.printf("FILA G/G/%d/%d\n", servidores, capacidade);
        System.out.printf("Tempo Global Final: %.4f\n", tempoGlobal);
        System.out.println("Clientes Perdidos: " + clientesPerdidos);
        System.out.printf("%-10s | %-15s | %-12s\n", "Estado", "Tempo Acum.", "Probabilidade");
        System.out.println("-------------------------------------------");

        for (int i = 0; i < temposAcumulados.length; i++) {
            double prob = (temposAcumulados[i] / tempoGlobal) * 100;
            System.out.printf("%-10d | %-15.4f | %.2f%%\n", i, temposAcumulados[i], prob);
        }
    }
}