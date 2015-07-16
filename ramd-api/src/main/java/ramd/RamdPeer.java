package ramd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * A Ramd cluster is made of Ramd peers which communicate with one
 * another through networking interfaces. Each Ramd peer is run in
 * a separate JVM. This class provides both a description and an
 * accessing handler to a particular peer.
 */
public class RamdPeer {
    // available socket addresses
    InetAddress _ip;
    int _port;
    private InetSocketAddress _sa;
    private ConcurrentSkipListMap<Integer, Task> _pendingTasks;

    RamdPeer(InetAddress ip, int port) {
        _ip = ip;
        _port = port;
        _sa = new InetSocketAddress(ip, port);
        _pendingTasks = new ConcurrentSkipListMap<Integer, Task>();
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
