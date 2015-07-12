package ramd;

import ramd.api.Schema;

import java.util.List;
import java.util.Map;

/**
 * A ramd request is the interface between the ramd core and the http server.
 * When the web server receives a http request, it calls {@code RamdRequest.build}
 * to generate a ramd request and append it to the ramd work queue. A successfully
 * built ramd request contains its handler information. Published ramd request
 * handlers, those registered through the {@code RamdRequest.registerHandler} API,
 * are matched against URI path to determine proper handling of an incoming http
 * request. If not registered, a handler can only be used internally.
 *
 * A ramd request is stateful. When not fully processed, the request's internal
 * state contains sufficient information to complete its work. Passing along
 * a stateful structure, serving a ramd request can be done in recursive fashion.
 */
public class RamdRequest {

    public static RamdRequest build(String path) throws Exception {
        return build(path, null, null);
    }

    public static RamdRequest build(String path, Map<String, List<String>> parms) throws Exception {
        return build(path, parms, null);
    }

    public static RamdRequest build(String pathStr, Map<String, List<String>> parms, String data) throws Exception {
        char[] path = pathStr.toCharArray();
        assert path[0] == '/';

        int x = 1;
        while (path[x++] == '/' && x < path.length);

        int y = x;
        while (y < path.length && path[y++] != '/');

        if (y == x) return null;

        String key = String.valueOf(path, x, y - x);
        RamdRequestHandler h = RamdRequestHandler.find(key);
        RamdRequest r = new RamdRequest(path, y, parms, data, h);

        if (h == null) {
            r._handler = Slash.handler();
            r._stack[0] = Key.ROOT;
            r._top = 1;
        }
        return r;
    }

    char[] _path;
    int _x;
    Map<String, List<String>> _parms;
    String _data;
    Schema _input;
    Schema _output;
    RamdRequestHandler _handler;
    Key[] _stack;
    int _top;

    private RamdRequest(char[] path,
                        int x,
                        Map<String, List<String>> parms,
                        String data,
                        RamdRequestHandler handler) {
        _path = path;
        _x = x;
        _parms = parms;
        _data = data;
        _handler = handler;
        _stack = new Key[16];
        _top = 0;
    }

    public RamdRequest handle() throws Exception {
        _handler = (RamdRequestHandler)_handler._mth.invoke(_handler._cached_handler, this);
        return this;
    }

    public boolean done() { return _handler == null; }

    // advance path cursor over next slashes.
    boolean skipSlashes() {
        char[] path = _path;
        int x = _x;

        while (path[x++] == '/' && x < path.length);

        if (x > _x) {
            _x = x;
            return true;
        } else
            return false;
    }

    String nextKey() {
        char[] path = _path;
        int x = _x;

        while (x < path.length && path[x++] != '/');

        if (x > _x) {
            String key = String.valueOf(path, _x, x - _x);
            _x = x;
            return key;
        } else
            return null;
    }
}
