package ramd.api;

import java.lang.reflect.Method;
import java.util.Map;

public class RamdRequestHandler {

    String _key;
    String _doc;
    Class  _cls;
    Method _mth;

    RamdRequestHandler (String key, String doc, Class  cls, Method mth) {
        _key = key;
        _doc = doc;
        _cls = cls;
        _mth = mth;
    }

    public static RamdRequestHandler build(String key, String doc, Class cls, String mth)
    throws Exception {
        for (Method m : cls.getMethods()) {
            if (m.getName().equals(mth)) {
                Class[] parms = m.getParameterTypes();
                if (parms != null
                        && parms.length == 2
                        && parms[0] == Integer.TYPE
                        && Schema.class.isAssignableFrom(parms[1])) {
                    return new RamdRequestHandler(key, doc, cls, m);
                }
            }
        }
        throw new Exception("Failed to build ramd request handler.");
    }

    private static Map<String, RamdRequestHandler> __registrar;

    public static RamdRequestHandler find(String key) {
        return __registrar.get(key);
    }

    public static void register(String key, String doc, Class  cls, String mth
    ) throws Exception {
        __registrar.put(key, build(key, doc, cls, mth));
    }
}
