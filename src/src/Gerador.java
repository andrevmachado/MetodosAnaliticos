public class Gerador {

    private long a = 1664525;
    private long c = 1013904223;
    private long M = (long) Math.pow(2, 32);
    private long seed = 12345;
    private int randomCount = 100000;

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