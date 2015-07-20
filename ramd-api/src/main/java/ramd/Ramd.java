package ramd;

import ramd.api.HttpRequetServer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Ramd {

    public static Logger logger;
    public static void log(String msg) {
        if (logger == null) logger = Logger.getLogger("RamdLog");
        logger.log(Level.INFO, msg);
    }
    public static void fail() throws Exception { fail("Unknown error"); }
    public static void fail(String err) throws Exception { throw new RamdException(err); }
    public static void halt(String msg) {
        System.exit(-1);
    }


    public static void main(String[] args) {
        try {
            //


            // Create API handling classes

            // start webserver
            HttpRequetServer.start(23456);
        } catch (Exception e) {

        }
    }
}
