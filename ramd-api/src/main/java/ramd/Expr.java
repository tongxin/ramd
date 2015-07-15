package ramd;

import java.nio.ByteBuffer;

public class Expr implements Packable<Expr> {

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return bb;
    }

    @Override
    public Expr unpack(ByteBuffer bb) {
        return this;
    }
}
