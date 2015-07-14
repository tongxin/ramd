package ramd;

import java.nio.ByteBuffer;

public class Dir extends Packable {

    // use parent key to retrieve the upper level directory structure
    private long _parentKey;
    // this reference, if not null, holds a cached reference of its parent Dir
    private transient Dir _parentDir;
    // the basename
    private String _name;
    // list the name-key mappings under this subdirectory
    private HalfSortedMap _ls;

    @Override
    ByteBuffer pack(ByteBuffer bb) {

        return bb;
    }

    @Override
    Packable unpack(byte[] bytes) {
        return null;
    }
}
