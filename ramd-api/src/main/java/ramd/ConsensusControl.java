package ramd;

import com.sun.rmi.rmid.ExecPermission;
import ramd.util.Util;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * The consensus control module.
 */
public class ConsensusControl {
    // Current cluster configuration
    static ClusterConfig cconfig;
    //
    static Map<Long,RamdPeer> peers;
    static Map<Long,RamdPeer> expats;
    static RamdPeer SELF;

    /**
     * Cluster configuration is the list of Ramd peers that everyone
     * acknowledges the status of everyone else. Change of this must be
     * initiated by the leader and be accepted by everyone.
     */
    static class ClusterConfig extends UDPMsg<ClusterConfig> {
        static byte udptype = UDPMsg.TYPEMAP.get(ClusterConfig.class);

        // The consensus epoch number. An epoch, aka a term, starts as a new
        // leader is elected and ends as the next leader election is proposed.
        int epoch;
        // The recognized peers plus myself, the premise that everyone should
        // agree on in the leader election process. Should be kept sorted.
        long[] _nodes;
        // leader index
        int leader;

        ClusterConfig() {
            super(udptype);
        }

        /**
         * short hash mainly used to exchange cluster config between peers
         */
        @Override
        public int hashCode() {
            long h = 0;
            for (long id : _nodes) h = Util.nextHash(h, id);
            return (int)((h >>> 32) ^ h);
        }

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public ClusterConfig unpack(ByteBuffer bb) {
            return null;
        }
    }

    /**
     * Propose a new term. This will happen after any new node tries to
     * join the group via this node or after timeout from leader's last
     * heartbeat. Since the election result is only dependent on the
     * common knowledge that everyone shares about their peers, it suffices
     * that the proposal only contains the next epoch number and the node
     * list.
     *
     * Alternative to proactive term change is quiescent consensus where
     * a consensus is naturally reached when everyone received heartbeat
     * from everyone else with the same cluster config.
     */
    private static void proposeNewTerm() {

    }

    /**
     * Cast a vote on next leader candidate, which is automatically
     * decided by the supplied peer list.
     */
    private static void vote(List<RamdPeer> peers) {

    }

    // This log is replicated on all peers driven by consensus algorithm.
    private static List<LogEntry> changeLog = new ArrayList<LogEntry>(100);

    static class LogEntry implements Packable<LogEntry> {
        int _seq;
        byte _actionType;
        Packable _action;

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public LogEntry unpack(ByteBuffer bb) {
            return null;
        }
    }

    static final byte LOG_ADDPEER = (byte)0;
    static final byte LOG_DELPEER = (byte)1;

    static class AddPeer implements Packable<AddPeer> {
        InetAddress _ip;
        int _port;

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public AddPeer unpack(ByteBuffer bb) {
            return null;
        }
    }

    static class DelPeer implements Packable<DelPeer> {
        InetAddress _ip;
        int _port;

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public DelPeer unpack(ByteBuffer bb) {
            return null;
        }
    }

    /**
     * Heartbeat messages are sent between leader and member only.
     */
    static class Heartbeat {
        // all heartbeat message sendings/receivings happen single-threadedly
        // so sharing a bytebuffer is safe.
        static ByteBuffer bb = ByteBuffer.allocate(1000);

        /**
         * Receive a heartbeat message from a peer.
         */
        static void recv(RamdPeer.Status s) throws Exception {
            long t = System.currentTimeMillis();
            long id = s._peerid;

            RamdPeer peer = peers.get(id);

            if (peer == null) { // first contact from this dude
                // put on the expat list anyway
                if (expats == null) expats = new HashMap<Long, RamdPeer>();
                try {
                    peer = RamdPeer.create(id);
                } catch (Exception e) {
                    Ramd.log("Received heartbeat with an broken id.");
                    return;
                }
                expats.put(id, peer);
                peer._status = s;
                peer._lasthb = t;
                // as soon as I've heard from this guy, send back my cluster config.
                peer.udpSend(cconfig.pack((ByteBuffer) bb.clear()));

                // announce the new comer
                
            }

            if ((Arrays.binarySearch(cconfig._nodes, id) >= 0) {

            }
        }

        static void send(long peer, RamdPeer.Status s) {

        }

        static void bcast(RamdPeer.Status s) throws Exception {
            if (s == null) return;
            RamdPeer.multcast(peers.values(), s.pack((ByteBuffer)bb.clear()));
        }

        public class Daemon extends Thread {
            public Daemon() {
                super("Heartbeat");
                setDaemon(true);
            }

            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                while (true) {
                    Runtime jvm = Runtime.getRuntime();
                    RamdPeer.Status s = SELF._status;
                    s._epoch = cconfig.epoch;
                    s._cchash = cconfig.hashCode();
                    s._num_cpus = jvm.availableProcessors();
                    s._tot_mem = (int)(jvm.totalMemory() >>> 20); // to MBytes
                    s._max_mem = (int)(jvm.maxMemory() >>> 20); // to MBytes
                    s._free_mem = (int)(jvm.freeMemory() >>> 20); // to MBytes_
                    try {
                        bcast(s);
                    } catch (Exception e) {
                        Ramd.log("Heartbeat messages failed to be multicasted.");
                        System.exit(-1);
                    }
                }
            }
        }

    }
}
