
package pbbroadcast.component;

import pbbroadcast.port.PbDeliver;
import se.sics.kompics.address.Address;
import unbroadcast.port.UnDeliver;

/**
 *
 * @author M&M
 */
public class LazyPbMsg extends UnDeliver {
    private PbDeliver pbDeliver;
    private int sn;

    public LazyPbMsg(Address src, PbDeliver pbDeliver, int sn) {
        super(src);
        this.pbDeliver = pbDeliver;
        this.sn = sn;
    }

    public PbDeliver getPbDeliver() {
        return pbDeliver;
    }

    public int getSn() {
        return sn;
    }
}

