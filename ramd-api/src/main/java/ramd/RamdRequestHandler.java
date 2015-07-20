package ramd;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

        // Verify that the handler class has the ``handler'' static method
        Method  getHandlerMethod = handler.check("handler", new Class[0]);

        if (method == null) Ramd.fail("Failed to build ramd request handler.");
        return new RamdRequestHandler(key, doc, cls, method, handler.build());
    }

    private static Map<String, RamdRequestHandler> __registry = new HashMap<String, RamdRequestHandler>();

    public static RamdRequestHandler find(String key) {
        return __registry.get(key);
    }

    public static void register(String key, String doc, Class  cls, String mth
    ) throws Exception {
        __registry.put(key, build(key, doc, cls, mth, new Class[]{RamdRequest.class}));
    }

    public static void register(RamdRequestHandler handler) {
        __registry.put(handler._key, handler);
    }

    static Class[] API_CLASSES = {
            Slash.class,
            StatusAPI.class
    };

    static {
        for (Class c : API_CLASSES) {
            if (!Checkable.class.isAssignableFrom(c)) {
                Ramd.log("Can't register non-Checkable Handler classs");
                continue;
            }
            try {
                Method getHandlerMethod = ((Class<? extends Checkable>) c).getMethod("handler", new Class[0]);
                RamdRequestHandler handler = (RamdRequestHandler) getHandlerMethod.invoke(null);
                __registry.put(handler._key, handler);

            } catch (NoSuchMethodException e) {
                Ramd.log("Failed to log RamdRequestHandler class " + c.getName());
            } catch (Exception e) {
                Ramd.log("Failed to invoke handler method on class " + c.getName());
            }

        }
    }
}
