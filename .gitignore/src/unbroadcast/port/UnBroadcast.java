
package unbroadcast.port;

import se.sics.kompics.Event;

/**
 *
 * @author M&M
 */
public class UnBroadcast extends Event {
    private UnDeliver UnDeliver;

    public UnBroadcast(UnDeliver UnDeliver) {
        this.UnDeliver = UnDeliver;
    }

    public UnDeliver getUnDeliver() {
        return UnDeliver;
    }
}

