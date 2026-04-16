public class Generator {

    private long a = 1664525;
    private long c = 1013904223;
    private long M = (long) Math.pow(2, 32);
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
