package ramd;

import ramd.util.Util;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The consensus control module.
 */
public class ConsensusControl {
    // Current cluster configuration
    static ClusterConfig cconfig;
    //
    static Map<Long,RamdPeer> peers;
    /**
     * Cluster configuration is the list of Ramd peers that everyone
     * acknowledges the status of everyone else. Change of this must be
     * initiated by the leader and be accepted by everyone.
     */
    static class ClusterConfig {
        // The consensus epoch number. An epoch, aka a term, starts as a new
        // leader is elected and ends as the next leader election is proposed.
        int epoch;
        // The recognized peers, the premise that everyone should agree on
        // in the leader election process. Should be kept sorted.
        long[] _nodes;
        // leader index
        int leader;

        /**
         * short hash mainly used to exchange cluster config between peers
         */
        @Override
        public int hashCode() {
            long h = 0;
            for (long id : _nodes) h = Util.nextHash(h, id);
            return (int)((h >>> 32) ^ h);
        }
    }

    /**
     * Propose a new leader election. This will happen after any new
     * node tries to join the group via this node. Since the election
     * result is only dependent on the common knowledge that everyone
     * shares about their peers, the proposal should be exactly the
     * the individual knowledge of the one who raised it.
     */
    private static void proposeElection() {

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
        long _peerid;
        RamdPeer.Status _status;

        /**
         * receive a heartbeat message from a node.
         */
        static void receive(Heartbeat h) {
            
        }

    }
}
