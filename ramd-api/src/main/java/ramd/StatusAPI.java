package ramd;

import ramd.api.Schema;

public class StatusAPI extends Checkable<StatusAPI> {

    @Override
    StatusAPI build() {

        return this;
    }

    public RamdRequestHandler getStatus(RamdRequest request) throws Exception {

        Schema output = request._output = Schema.build(new String[]{"ip", "port", "mem"});

        output.addRow(new String[]{"localhost", "23456", "1024m"}); // test only

        request._handler = null;

        return null ;
    }

    private static RamdRequestHandler __handler = null;

    public static RamdRequestHandler handler() throws Exception {
        if (__handler == null) {
            synchronized (StatusAPI.class) {
                if (__handler == null)
                    __handler = RamdRequestHandler.build("Status",
                            "Get current system status of this Ramd node",
                            StatusAPI.class,
                            "getStatus",
                            new Class[]{RamdRequestHandler.class, RamdRequest.class});

            }
        }
        return __handler;
    }
}
