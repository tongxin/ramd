package ramd.api;

import java.lang.reflect.Method;
import java.util.Map;

public class RamdRequest {

    public static RamdRequest parseRequest(String path) {
        return new RamdRequest();
    }


    public static HandlerRegistration registerHandler(
            String key,
            String doc,
            Class  cls,
            String mth
    ) throws Exception {
        HandlerRegistration handler = null;
        for (Method m : cls.getMethods()) {
            if (m.getName().equals(mth)) {
                Class[] parms = m.getParameterTypes();
                if (parms != null
                        && parms.length == 2
                        && parms[0] == Integer.TYPE
                        && Schema.class.isAssignableFrom(parms[1])) {
                    handler = new HandlerRegistration(key, doc, cls, m);
                    __registrar.put(key, handler);
                    break;
                }
            }
        }

        if (handler == null)
            throw new Exception("Handler method not found in the handler class.");

        return handler;
    }

    private static Map<String, HandlerRegistration> __registrar;

    static class HandlerRegistration {
        public String _key;
        public String _doc;
        public Class  _cls;
        public Method _mth;

        public HandlerRegistration(
                String key,
                String doc,
                Class  cls,
                Method mth
        ) {
            _key = key;
            _doc = doc;
            _cls = cls;
            _mth = mth;
        }
    }

}
