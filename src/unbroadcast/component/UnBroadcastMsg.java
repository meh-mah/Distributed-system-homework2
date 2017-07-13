
package unbroadcast.component;

import id2203.link.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;
import unbroadcast.port.UnDeliver;

/**
 *
 * @author M&M
 */
public class UnBroadcastMsg extends Flp2pDeliver {
    private UnDeliver UnDeliver;

    public UnBroadcastMsg(Address src, UnDeliver UnDeliver) {
        super(src);
        this.UnDeliver = UnDeliver;
    }

    /**
     *
     * @return
     */
    public UnDeliver getUnDeliver() {
        return UnDeliver;
    }
}
