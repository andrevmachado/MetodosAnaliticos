import java.util.List;

public class Generator {

    //alteramos os valores conforme disse o professor
    private long a = 1687454;
    private long c = 1048724884;
    private long M = 1801007006028L;
    private long seed;
    private int randomCount;
    private List<Double> predefinedRandoms;
    private int currentIndex = 0;

    public Generator(long seed, int randomLimit) {
        this.seed = seed;
        this.randomCount = randomLimit;
        this.predefinedRandoms = null;
    }

    public Generator(List<Double> predefinedRandoms) {
        this.predefinedRandoms = predefinedRandoms;
        this.randomCount = predefinedRandoms.size();
    }

    public double nextRandom() {
        if (randomCount <= 0) return -1;
        
        if (predefinedRandoms != null) {
            double rnd = predefinedRandoms.get(currentIndex++);
            randomCount--;
            return rnd;
        } else {
            seed = (a * seed + c) % M;
            randomCount--;
            return (double) seed / M;
        }
    }

    public boolean hasRandom() {
        return randomCount > 0;
    }
}
