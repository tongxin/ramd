package ramd;

import java.nio.ByteBuffer;

public interface Packable<P extends Packable> {

    ByteBuffer pack(ByteBuffer bb);
    P unpack(byte[] bytes);
}
