public class Evento implements Comparable<Evento> {

    public static final int CHEGADA = 0;
    public static final int SAIDA = 1;

    double tempo;
    int tipo;

    public Evento(double tempo, int tipo) {
        this.tempo = tempo;
        this.tipo = tipo;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempo, outro.tempo);
    }
}