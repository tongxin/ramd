package ramd;

/**
 * The consensus control module.
 */
public class ConsensusControl {

    // The consensus epoch number. An epoch, aka a term, starts as a new
    // leader is elected and ends as the next leader election is proposed.
    static int epoch;
    // Current leader
    static RamdPeer leader;
    // The recognized peers, the premise that everyone should agree on
    // in the leader election process.
    static List<RamdPeer> peers;

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
    }

    static final byte LOG_ADDPEER = (byte)0;
    static final byte LOG_DELPEER = (byte)1;

    static class AddPeer implements Packable<AddPeer> {
        InetAddress _ip;
        int _port;
    }

    static class DelPeer implements Packable<DelPeer> {
        InetAddress _ip;
        int _port;
    }


    /**
     * Heartbeat messages are sent between leader and member only.
     */
    static class Heartbeat {


    }
}
