package ramd;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class defining a UDP message
 * @param <T> the real UDP message type
 */
public abstract class UDPMsg<T extends Packable> implements Packable<T> {
    public final byte _type;
    static Class[] TYPES = {
                RamdPeer.Status.class
    };

    static Map<Class, Byte> TYPEMAP;

    static {
        TYPEMAP = new HashMap<Class, Byte>();
        for (int i = 0; i < TYPES.length; i++)
            TYPEMAP.put(TYPES[i], (byte)i);
    }

    UDPMsg(byte type) {
        _type = type;
    }
}
