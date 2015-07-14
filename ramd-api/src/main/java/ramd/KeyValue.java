package ramd;

import java.util.Objects;

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
final public class KeyValue extends Packable implements Comparable {

    // These are the value types what Ramd can recognize at the top level
    public static byte TYPE_NONE = 0;
    public static byte TYPE_DATA = 1;
    public static byte TYPE_DIR  = 2;
    public static byte TYPE_LIST = 3;
    public static byte TYPE_VEC  = 4;
    public static byte TYPE_BLK  = 5;
    public static byte TYPE_EXPR = 6;

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
     * The raw value bytes
     */
    private volatile Packable _value;

    // get value type API
    public boolean isData()  { return TYPE_DATA == _type; }
    public boolean isDir ()  { return TYPE_DIR  == _type; }
    public boolean isList()  { return TYPE_LIST == _type; }
    public boolean isVec ()  { return TYPE_VEC  == _type; }
    public boolean isBlk ()  { return TYPE_BLK  == _type; }
    public boolean isExpr()  { return TYPE_EXPR == _type; }

    public KeyValue open(String basename) throws Exception {
        if (!isDir())  Ramd.fail("The kvnode is not a directory. ");


    }

    public static KeyValue ROOT;
}
