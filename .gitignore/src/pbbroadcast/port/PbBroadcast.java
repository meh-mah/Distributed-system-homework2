
package pbbroadcast.port;

import se.sics.kompics.Event;

/**
 *
 * @author M&M
 */
public class PbBroadcast extends Event {
    private PbDeliver PbDeliver;

    public PbBroadcast(PbDeliver PbDeliver) {
        this.PbDeliver = PbDeliver;
    }

    public PbDeliver getPbDeliver() {
        return PbDeliver;
    }
}