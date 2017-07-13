
package application;

import pbbroadcast.port.PbDeliver;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public class PbDeliverMessage extends PbDeliver {
    private String message;

    public PbDeliverMessage(Address src, String msg) {
        super(src);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
