package ramd;

/*
 * Ramd's K/V store is hierarchical, similar to a program's namespace and a file system
 * directory. The Slash class defines ``slash'', the directory lookup service.
 */
public class Slash extends Checkable<Slash> {

    @Override
    public Slash build() {

        return this;
    }


    @RequestHandler
    public RamdRequestHandler slash(RamdRequest request) throws Exception {
        int top = request._top;
        long key = request._stack[top];

        KeyValue kv = DMap.get(key);

        return null;
    }

    private static RamdRequestHandler __handler = null;

    public static RamdRequestHandler handler() throws Exception {
        if (__handler == null) {
            synchronized (Slash.class) {
                if (__handler == null)
                    __handler = RamdRequestHandler.build("/",
                            "The ramd k/v store subdirectory(subspace) handler",
                            Slash.class,
                            "slash",
                            new Class[]{RamdRequestHandler.class, RamdRequest.class});

            }
        }
        return __handler;
    }
}
