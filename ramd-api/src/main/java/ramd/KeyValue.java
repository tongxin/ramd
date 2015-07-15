package ramd;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Ramd stores a key value pair in one piece of memory space to save
 * memory management operations. The entire keyvalue record is comprised of
 * following parts: data type, the access control info, the partition id,
 * hierarchical info and the raw value bytes. Interpretation of the value
 * bytes is subject to specific data type handlers. When keys are generated,
 * they are assigned to partitions which are the basis for data distribution,
 * replication and backup.  The partition to host map is maintained on each
 * host using a distributed consensus algorithm.
 *
 * Key hashcodes are encoded with key types and partition ids to facilitate fast
 * lookup operation.
 */
final public class KeyValue implements Packable<KeyValue> {

    // These are the value types what Ramd can recognize at the top level
    public final static byte TYPE_NONE = 0;
    public final static byte TYPE_TEXT = 1;
    public final static byte TYPE_DIR  = 2;
    public final static byte TYPE_TAB  = 3;
    public final static byte TYPE_VEC  = 4;
    public final static byte TYPE_BLK  = 5;
    public final static byte TYPE_EXPR = 6;

    public final static Class[] TYPES = {
            Value.class,
            Text.class,
            Dir.class,
            Tab.class,
            Vec.class,
            Blk.class,
            Expr.class
    };

    public static Map<String, Integer> TYPEMAP;

    static {
        for (int i = 0; i < TYPES.length; i++)
            TYPEMAP.put(TYPES[i].getName(), i);
    }

    /**
     * The eight byte key uniquely identifies a Ramd keyvalue record.
     * Compared to variable length keys widely adopted in other key value stores
     * fixed length keys save space meanwhile separate the names(paths) and
     * the referenced data. Path renaming won't result in store wide remapping.
     */
    private long _key;

    /**
     * The partition id
     */
    private int _part;

    /**
     * The data type
     */
    private byte _type;

    /**
     * The access control info
     */
    private int _perm;

    /**
     * The last modification timestamp
     */
    private long _ts;

    /**
     * The value object
     */
    private volatile Packable _val;


    // get value type API
    public boolean isText()  { return TYPE_TEXT == _type; }
    public boolean isDir ()  { return TYPE_DIR  == _type; }
    public boolean isTab ()  { return TYPE_TAB  == _type; }
    public boolean isVec ()  { return TYPE_VEC  == _type; }
    public boolean isBlk ()  { return TYPE_BLK  == _type; }
    public boolean isExpr()  { return TYPE_EXPR == _type; }

    public Class<? extends Packable> getValueType() {
        return TYPES[_type];
    }

    /**
     * Serialization API
     * @param bb
     * @return
     */
    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        bb.putLong(_key);
        bb.putInt(_part);
        bb.put(_type);
        bb.putInt(_perm);
        bb.putLong(_ts);
        if (_val != null) _val.pack(bb);
        return bb;
    }

    /**
     * Deserialization API
     * @param bb
     * @return
     */
    @Override
    public KeyValue unpack(ByteBuffer bb) {
        this._key = bb.getLong();
        this._part = bb.getInt();
        this._type = bb.get();
        this._perm = bb.getInt();
        this._ts = bb.getLong();
        try {
            this._val = getValueType().newInstance().unpack(bb);
        } catch (Exception e) {
            this._val = null;
        }
        return this;
    }

    public static KeyValue ROOT;
}
