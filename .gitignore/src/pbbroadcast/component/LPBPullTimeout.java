
package pbbroadcast.component;

import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 *
 * @author M&M
 */
public class LPBPullTimeout extends Timeout {
    private Address bSource;
    private int sn;

    public LPBPullTimeout(ScheduleTimeout r, Address bSource, int sn) {
        super(r);
        this.bSource = bSource;
        this.sn = sn;
    }

    public Address getBSource() {
        return bSource;
    }

    public int getSn() {
        return sn;
    }
}

