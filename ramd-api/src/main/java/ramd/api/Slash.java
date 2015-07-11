package ramd.api;

public class Slash extends Checkable<Slash> {
//    private Slash() {
//            super("/",
//                    "The K/V store resource handler",
//                    Slash.class,
//                    null);
//        try {
//            _mth = Slash.class.getMethod("handle", new Class[]{RamdRequestHandler.class, RamdRequest.class});
//        } catch (Exception e) {}
//    }

    @Override
    Slash build() {
        
    }

    public RamdRequestHandler handle(RamdRequest request) { return null; }

    public static Slash Handler = new Slash();
}
