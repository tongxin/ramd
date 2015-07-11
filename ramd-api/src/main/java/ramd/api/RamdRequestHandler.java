package ramd.api;

import com.sun.tools.javac.comp.Check;
import ramd.Ramd;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RamdRequestHandler {

    String _key;
    String _doc;
    Class<? extends Checkable> _cls;
    Method _mth;
    Object _cached_handler;

    RamdRequestHandler (String key, String doc, Class<? extends Checkable> cls, Method mth, Object h) {
        _key = key;
        _doc = doc;
        _cls = cls;
        _mth = mth;
        _cached_handler = h;
    }

    public static RamdRequestHandler build(String key, String doc, Class<? extends Checkable> cls,
                                           String mth, Class[] ptypes)
    throws Exception {

        Checkable handler = cls.newInstance();
        Method  method = handler.check(mth, ptypes);

        if (method == null) Ramd.fail("Failed to build ramd request handler.");
        else
            return new RamdRequestHandler(key, doc, cls, method, handler.build());
    }

    private static Map<String, RamdRequestHandler> __registry = new HashMap<String, RamdRequestHandler>();

    public static RamdRequestHandler find(String key) {
        return __registry.get(key);
    }

    public static void register(String key, String doc, Class  cls, String mth
    ) throws Exception {
        __registry.put(key, build(key, doc, cls, mth, new Class[]{RamdRequestHandler.class, RamdRequest.class}));
    }
}
