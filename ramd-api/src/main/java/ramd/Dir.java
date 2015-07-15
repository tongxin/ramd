package ramd;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Dir implements Packable<Dir> {
    private transient KeyValue _kv;
    // use parent key to retrieve the upper level directory structure
    private long _parentKey;
    // this reference, if not null, holds a cached reference of its parent Dir
    private transient Dir _parentDir;
    // the basename
    private String _name;
    // list the name-key mappings under this subdirectory
    private SortedMap<String, Long> _ls;

    Dir(long parentKey, String name) {
        _kv = null;
        _parentKey = parentKey;
        _parentDir = null;
        _name = name;
        _ls = new ConcurrentSkipListMap<String,Long>();
    }

    Dir setParentDir(Dir parent) {
        _parentDir = parent;
        return this;
    }

    public Long[] ls() {
        return (Long[])_ls.values().toArray();
    }

    public Map.Entry<String, Long>[] ll() {
        return (Map.Entry<String,Long>[])_ls.entrySet().toArray();
    }

    public Long find(String name) {
        return _ls.get(name);
    }

    public String basename() {
        return _name;
    }

    @Override
    public ByteBuffer pack(ByteBuffer bb) {

        return bb;
    }

    @Override
    public Dir unpack(ByteBuffer bb) {

        return this;
    }

    static Long ROOTKEY = null;

    /**
     * build the root Dir, must be called before accepting any ramd requests.
     * The value of rootkey is decided by the Ramd group consensus leader and
     * circulated to all the member nodes.
     */
    static void buildRoot(long rootkey) {
        if (ROOTKEY != null)  return;

        ROOTKEY = rootkey;

    }
}
