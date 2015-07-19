package ramd;

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
    static RamdPeer LEADER;
    static boolean hibernate = false;

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
     * Member reports to leader on seeing someone
     */
    static class Report extends UDPMsg<Report> {
        static byte udptype = UDPMsg.TYPEMAP.get(Report.class);
        static Report msg;

        long _expatid;

        Report() {
            super(udptype);
        }

        Report setNewPeer(long peer) {
            _expatid = peer;
            return this;
        }

        static Report getMsg() {
            if (msg == null) msg = new Report();
            return msg;
        }

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public Report unpack(ByteBuffer bb) {
            return null;
        }
    }

    /**
     * Leader announcement
     */
    static class Announce extends UDPMsg<Announce> {
        static byte udptype = UDPMsg.TYPEMAP.get(Announce.class);
        static Announce msg;

        int _epoch;
        long[] _nodes;

        Announce() {
            super(udptype);
        }

        static Announce getMsg() {
            if (msg == null) msg = new Announce();
            return msg;
        }

        Announce setPeers(long[] ps, long p) {
            long[] nodes = Arrays.copyOf(ps, ps.length+1);
            nodes[nodes.length-1] = p;
            return this;
        }

        Announce setEpoch(int epoch) {
            _epoch = epoch;
            return this;
        }

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return bb;
        }

        @Override
        public Announce unpack(ByteBuffer bb) {
            return null;
        }
    }

    static class Inform extends UDPMsg<Inform> {
        static byte udptype = UDPMsg.TYPEMAP.get(Inform.class);

        Inform() {
            super(udptype);
        }

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public Inform unpack(ByteBuffer bb) {
            return null;
        }
    }

    /**
     * Propose a new term. This will happen after heartbeat timeout from
     * the leader. Any node who loses contact with the leader, meaning
     * the leader and this node are isolated in two disconnected network
     * partitions, broadcasts this Propose message to everyone it can reach.
     * The result is the nodes in the same partition get to know their
     * situation soon. Dead nodes are treated as the same as an disconnected
     * single node partitions.
     *
     * Once the nodes in the newly formed partition gathers the new config
     * info, and of course as of then they should've agreed on the new
     * leader, they will decide whether to hibernate indefinitely due to
     * missing backups in the partition. When hibernate, it's not necessary
     * to distinguish a leader follower difference between the nodes.
     *
     * However, if the new partition is lucky enough to have a complete
     * backup set, which is only possible when the partition occupies
     * a majority of nodes, the new leader will start exercising its role.
     *
     * Alternative to proactive term change is quiescent consensus where
     * a consensus is naturally reached when everyone received heartbeat
     * from everyone else with the same cluster config.
     */
    static class Propose extends UDPMsg<Propose> {
        static byte udptype = UDPMsg.TYPEMAP.get(Propose.class);

        Propose() {
            super(udptype);
        }

        @Override
        public ByteBuffer pack(ByteBuffer bb) {
            return null;
        }

        @Override
        public Propose unpack(ByteBuffer bb) {
            return null;
        }
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
            // Remain passive to any heartbeat messages if in a hibernate mode.
            if (hibernate) return;

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

                // Now report the new comer to the leader who will officially announce
                // the enrollment once it receives reports from all the members
                if (LEADER != SELF) {
                    // Inform this guy of my current cluster config.
                    peer.udpSend(cconfig.pack((ByteBuffer) bb.clear()));
                    LEADER.udpSend(Report.getMsg().setNewPeer(id).pack((ByteBuffer) bb.clear()));
                    return;
                }
            }

            // Leader reaches here, common path
            if (Arrays.binarySearch(cconfig._nodes, id) >= 0) {
                // bro's heartbeat
                peer._lasthb = t;
                return;
            }

            // Single node active partition ? then enroll this guy immediately.
            if (cconfig._nodes.length == 1) {
                Announce msg = Announce.getMsg().setPeers(cconfig._nodes, id).setEpoch(cconfig.epoch+1);
                peer.udpSend(msg.pack((ByteBuffer)bb.clear()));
            }
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
                while (!hibernate) {
                    Runtime jvm = Runtime.getRuntime();
                    RamdPeer.Status s = SELF._status;
                    s._epoch = cconfig.epoch;
                    s._cchash = cconfig.hashCode();
                    s._num_cpus = jvm.availableProcessors();
                    s._tot_mem = (int)(jvm.totalMemory() >>> 20); // to MBytes
                    s._max_mem = (int)(jvm.maxMemory() >>> 20); // to MBytes
                    s._free_mem = (int)(jvm.freeMemory() >>> 20); // to MBytes_
                    try {
                        LEADER.udpSend(s.pack((ByteBuffer) bb.clear()));
                    } catch (Exception e) {
                        Ramd.log("Heartbeat messages failed to be sent.");
                        System.exit(-1);
                    }
                }
            }
        }

    }
}
