public class Generator {

    //alteramos os valores conforme disse o professor
    private long a = 1687454;
    private long c = 1048724884;
    private long M = 1801007006028L;
    private long seed;
    private int randomCount;

    public Generator(long seed, int randomLimit) {
        this.seed = seed;
        this.randomCount = randomLimit;
    }

    public double nextRandom() {
        if (randomCount <= 0) return -1;
        seed = (a * seed + c) % M;
        randomCount--;
        return (double) seed / M;
    }

    public boolean hasRandom() {
        return randomCount > 0;
    }
}
