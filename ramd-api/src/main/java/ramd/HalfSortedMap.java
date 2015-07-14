package ramd;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HalfSortedMap<K, V> implements Packable<HalfSortedMap>{

    private int[]    _sortedHashCodes;
    private Object[] _sortedKeys;
    private Object[] _sortedVals;
    private Object[] _unsortedKeys;
    private Object[] _unsortedVals;
    private int      _unsortedCount;

    private transient long _misses;
    private transient long _hits;

    public HalfSortedMap(int capacity) {
        _sortedHashCodes = new int[0];
        _sortedKeys = new Object[0];
        _sortedVals = new Object[0];
        _unsortedKeys = new Object[capacity];
        _unsortedVals = new Object[capacity];
        _unsortedCount = 0;
        _misses = 0;
        _hits = 0;
    }

    /**
     * Get a value from a HalfSortedMap.
     * @param key query key
     * @return found value
     * @throws Exception there's rare case when the input key crashes with some key's hashcode in the map
     */
    public V get(K key) throws Exception {
        int x = Arrays.binarySearch(_sortedHashCodes, key.hashCode());
        if (x >= 0) {
            if (key.equals(_sortedKeys[x])) {
                // hitting the sorted counts as a hit
                ++_hits;
                return (V) _sortedVals[x];
            } else
                throw new Exception("Oops.. there's rare hashcode conflict in this HalfSortedMap.");
        }
        // if miss the sorted map, search unsorted using brute force
        for (int i = 0; i < _unsortedCount; i++) {
            if (((K)_unsortedKeys[i]).equals(key)) {
                // hitting the unsorted counts as a miss
                V val = (V)_unsortedVals[i];
                if ((++_misses / (double)(_misses+_hits)) > 0.1)
                    sort();
                return val;
            }
        }
        // not found
        return null;
    }

    // Take the unsorted and merged into the sorted
    private void sort() {

    }

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return null;
    }

    @Override
    public HalfSortedMap unpack(byte[] bytes) {
        return null;
    }
}
