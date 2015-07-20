package ramd;

import ramd.api.Schema;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * A ramd request is the external description of a job. The RamdRequest class
 * serves as interface between the ramd core and the http server.
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
public class RamdRequest implements Packable<RamdRequest> {

    public static RamdRequest build(String path) throws Exception {
        return build(path, null, null);
    }

    public static RamdRequest build(String path, Map<String, List<String>> parms) throws Exception {
        return build(path, parms, null);
    }

    public static RamdRequest build(String pathStr, Map<String, List<String>> parms, String data) throws Exception {
        char[] path = pathStr.toCharArray();
        assert path[0] == '/';

        int x = 0;
        while (path[x] == '/' && x < path.length) x++;

        int y = x;
        while (y < path.length && path[y] != '/') y++;

        if (y == x) return null;

        String key = String.valueOf(path, x, y - x);
        RamdRequestHandler h = RamdRequestHandler.find(key);
        RamdRequest r = new RamdRequest(path, y, parms, data, h);

        if (h == null) {
            r._handler = Slash.handler();
            r._stack[0] = Dir.ROOTKEY;
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
    long[] _stack;
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
        _stack = new long[16];
        _top = 0;
    }

    public RamdRequest handle() throws Exception {
        _handler = (RamdRequestHandler)_handler._mth.invoke(_handler._cached_handler, this);
        return this;
    }

    public boolean done() { return _handler == null; }

    public String getJson() {
        if (!done()) return null;
        return _output.toJson();
    }

    // advance path cursor over next slashes.
    boolean skipSlashes() {
        char[] path = _path;
        int x = _x;

        while (path[x] == '/' && x < path.length) x++;

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

    @Override
    public ByteBuffer pack(ByteBuffer bb) {
        return null;
    }

    @Override
    public RamdRequest unpack(ByteBuffer bb) {
        return null;
    }
}
