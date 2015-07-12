package ramd;

/*
 * Ramd's K/V store is hierarchical, similar to a program's namespace and a file system
 * directory. The Slash class defines handling entering and creating sub-namespace.
 */
public class Slash extends Checkable<Slash> {

    @Override
    public Slash build() {

        return this;
    }

    public RamdRequestHandler slash(RamdRequest request) {

    }

    private Dir slash(Key key) {}

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
