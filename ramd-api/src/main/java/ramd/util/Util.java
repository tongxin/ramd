package ramd.util;

/**
 * Common utility functions
 */
public class Util {

    /**
     * Get next hash value
     * @param h previous hash
     * @param x next value to hash
     * @return new hash
     */
    public static long nextHash(long h, long x) {
        return ((h << 5) + h) ^ x;
    }

}
