package ramd;

import java.nio.ByteBuffer;

/**
 * Internal to Ramd, task is unit of computation. A Task is location oblivious
 * and self contained. Submitting the same task from any peer would mean the
 * same logic to be carried out over the distributed key value stores.
 */
public class Task implements Packable<Task> {

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return null;
    }

    @Override
    public Task unpack(ByteBuffer bb) {
        return null;
    }
}

