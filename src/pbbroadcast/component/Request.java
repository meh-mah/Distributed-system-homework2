

package pbbroadcast.component;

import id2203.link.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public class Request extends Flp2pDeliver {
    private Address bSource;
    private int sn;
    public int maxround;

    public Request(Address src, Address bSource, int sn, int maxround) {
        super(src);
        this.bSource = bSource;
        this.sn = sn;
        this.maxround = maxround;
    }

    public Address getBSource() {
        return bSource;
    }

    public int getSn() {
        return sn;
    }

    @Override
    public String toString() {
        return "Request from source=" + super.getSource() + ":: Max round=" + maxround+":: B Source=" + bSource + ":: sn=" + sn;
    }
}

