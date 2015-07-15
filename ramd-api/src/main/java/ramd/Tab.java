package ramd;

import java.nio.ByteBuffer;

public class Tab implements Packable<Tab> {

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return bb;
    }

    @Override
    public Tab unpack(ByteBuffer bb) {
        return this;
    }
}
