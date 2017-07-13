
package pbbroadcast.port;

import java.io.Serializable;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public abstract class PbDeliver extends Event implements Serializable {
    private Address src;

    public PbDeliver(Address src) {
        this.src = src;
    }

    public Address getSrc() {
        return src;
    }
}
