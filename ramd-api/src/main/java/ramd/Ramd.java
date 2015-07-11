package ramd;

public class Ramd {

    public static void fail() throws Exception { fail("Unknown error"); }
    public static void fail(String err) throws Exception { throw new RamdException(err); }
}
