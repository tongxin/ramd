package ramd;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Ramd {

    public static Logger logger;
    public static void log(String msg) {
        logger.log(Level.INFO, msg);
    }
    public static void fail() throws Exception { fail("Unknown error"); }
    public static void fail(String err) throws Exception { throw new RamdException(err); }
}
