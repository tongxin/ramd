package ramd.api;

import java.util.List;
import java.util.Map;

/**
 * A ramd request is the interface between the ramd core and the http server.
 * When the web server receives a http request, it calls {@code RamdRequest.build}
 * to generate a ramd request and append it to the ramd work queue. A successfully
 * built ramd request contains its handler information. All ramd request handlers
 * should be registered through the {@code RamdRequest.registerHandler} API.
 *
 */
public class RamdRequest {
    public static enum STATUS {
        INCOMPLETE, COMPLETE
    }

    public static RamdRequest build(String path) {
        return build(path, null, null);
    }

    public static RamdRequest build(String path,
                                    Map<String, List<String>> parms) {
        return build(path, parms, null);
    }

    public static RamdRequest build(String pathStr,
                                    Map<String, List<String>> parms,
                                    String data) {

        char[] path = pathStr.toCharArray();
        assert path[0] == '/';

        int x = 1;
        while (path[x++] == '/' && x < path.length);

        int y = x;
        while (y < path.length && path[y++] != '/');

        if (y > x) {
            String key = String.valueOf(path, x, y - x);
            RamdRequestHandler h = RamdRequestHandler.find(key);
            if (h == null) h = SlashHandler.Handler;
            return new RamdRequest(path, y, parms, data, h);
        }
        return  null;
    }

    private STATUS _status;
    private char[] _path;
    private int _x;
    private Map<String, List<String>> _parms;
    private String _data;
    private Schema _input;
    private Schema _output;
    private RamdRequestHandler _handler;

    private RamdRequest(char[] path,
                        int x,
                        Map<String, List<String>> parms,
                        String data,
                        RamdRequestHandler handler) {
        _status = STATUS.INCOMPLETE;
        _path = path;
        _x = x;
        _parms = parms;
        _data = data;
        _handler = handler;
    }

    public RamdRequest handle() {

        return this;
    }

    private boolean slash() {
        if (_path[_x] == '/') {
            ++_x;
            return true;
        }
        return false;
    }


}
