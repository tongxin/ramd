package ramd.util;

import java.net.InetAddress;

public class InetUtil {

    /**
     * Generate unique id from ipv4 and port number
     * @param ip
     * @param port
     * @return id in long
     */
    public static long ip2id(InetAddress ip, int port) {
        byte[] bytes = ip.getAddress();
        assert port < 65536;

        return bytes[0]&0xFFL
                + (bytes[1]&0xFFL) << 8
                + (bytes[2]&0xFFL) << 16
                + (bytes[3]&0xFFL) << 24
                + ((long)port)&0xFFFFL << 32;
    }

    public static byte[] id2bytes(long id) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte)(id & 0xFF);
        bytes[1] = (byte)((id >>> 8) & 0xFF);
        bytes[2] = (byte)((id >>> 16) & 0xFF);
        bytes[3] = (byte)((id >>> 24) & 0xFF);
        bytes[4] = (byte)((id >>> 32) & 0xFF);
        bytes[5] = (byte)((id >>> 36) & 0xFF);
        return bytes;
    }

    public static InetAddress id2ip(long id) throws Exception {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(id & 0xFF);
        bytes[1] = (byte)((id >>> 8) & 0xFF);
        bytes[2] = (byte)((id >>> 16) & 0xFF);
        bytes[3] = (byte)((id >>> 24) & 0xFF);
        return InetAddress.getByAddress(bytes);
    }

    public static int id2port(long id) {
        return (int)((id >>> 32) & 0xFFFF);
    }
}
