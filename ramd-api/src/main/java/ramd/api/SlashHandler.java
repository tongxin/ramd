package ramd.api;

public class SlashHandler extends RamdRequestHandler {
    private SlashHandler() {
            super("/",
                    "The K/V store resource handler",
                    SlashHandler.class,
                    null);
        try {
            _mth = SlashHandler.class.getMethod("handle", new Class[]{RamdRequestHandler.class, RamdRequest.class});
        } catch (Exception e) {}
    }

    public RamdRequestHandler handle(RamdRequest request) { return null; }

    public static SlashHandler Handler = new SlashHandler();
}
