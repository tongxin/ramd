package ramd;

import java.nio.ByteBuffer;

public class Text implements Packable<Text> {

    private String _str;

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return null;
    }

    @Override
    public Text unpack(ByteBuffer bb) {
        return null;
    }
}
