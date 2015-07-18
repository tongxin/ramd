package ramd;

import ramd.util.InetUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * A Ramd cluster is made of Ramd peers which communicate with one
 * another through networking interfaces. Each Ramd peer is run in
 * a separate JVM. This class provides both a description and an
 * accessing handler to a particular peer.
 */
public class RamdPeer {

    public static DatagramChannel UDP_Chan;

    public static void multcast(Collection<RamdPeer> nodes, ByteBuffer bb) throws Exception {
        if (UDP_Chan == null) try {
            UDP_Chan = DatagramChannel.open();
        } catch (IOException e) {
            Ramd.fail("Openning UDP channel failed.");
        }
        for (RamdPeer node : nodes)
            UDP_Chan.send(bb, node._sa);
    }

    /**
     * The peer id used to key'ed into cluster configuration and other
     * collective member info.
     */
    long _id;

    // available socket addresses
    volatile Status _status;
    private InetSocketAddress _sa;
    private ConcurrentSkipListMap<Integer, Task> _pendingTasks;

    RamdPeer(InetAddress ip, int port) {
        _id = InetUtil.ip2id(ip, port);
        _sa = new InetSocketAddress(ip, port);
        _pendingTasks = new ConcurrentSkipListMap<Integer, Task>();
    }

    public static class Status implements Packable<Status> {
        long _peerid;
        // consensus epoch
        int _epoch;
        // cluster config hash
        int _cchash;
//        int _jar_hash;
        int _num_cpus;
        int _tot_mem;
        int _max_mem;
        int _free_mem;

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public Status unpack(ByteBuffer bb) {
            return null;
        }
    }

    // reusable sockets
    private List<SocketChannel> _freeSocks = new ArrayList<SocketChannel>();

    SocketChannel getSocket() throws IOException {
        SocketChannel sock = null;
        // try to reuse a socket
        if (!_freeSocks.isEmpty()) {
            synchronized (this) {
                if (!_freeSocks.isEmpty()) {
                    sock = _freeSocks.remove(_freeSocks.size() - 1);
                    if (sock.isOpen())
                        return sock;
                }
            }
        }
        // new socket
        sock = SocketChannel.open();
        sock.socket().setReuseAddress(true);
        sock.connect(_sa);
        return sock;
    }

    void freeSocket(SocketChannel sock) {
        if (!sock.isOpen())
            return;
        synchronized (this) {
            _freeSocks.add(sock);
        }
    }


}
