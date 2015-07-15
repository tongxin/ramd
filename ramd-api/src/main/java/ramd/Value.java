package ramd;

import java.nio.ByteBuffer;

public class Value implements Packable<Value> {

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return bb;
    }

    @Override
    public Value unpack(ByteBuffer bb) {
        return this;
    }
}
