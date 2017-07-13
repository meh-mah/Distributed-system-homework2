
package unbroadcast.component;

import id2203.link.flp2p.FairLossPointToPointLink;
import id2203.link.flp2p.Flp2pSend;
import java.util.Set;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import unbroadcast.port.UnBroadcast;
import unbroadcast.port.UnBroadcastPort;

/**
 *
 * @author M&M
 */
public class UnBroadcastComponent extends ComponentDefinition {

    Negative<UnBroadcastPort> unPort = provides(UnBroadcastPort.class);
    Positive<FairLossPointToPointLink> flp2pPort = requires(FairLossPointToPointLink.class);

    private Address myAddress;
    private Set<Address> neighbors;

    public UnBroadcastComponent() {
        subscribe(hMessage, flp2pPort);
        subscribe(hInit, control);
        subscribe(hUnBroadcast, unPort);
        
    }

    Handler<UnBroadcastInit> hInit = new Handler<UnBroadcastInit>() {
        @Override
        public void handle(UnBroadcastInit e) {
            myAddress = e.getMyAddress();
            neighbors = e.getNeighbors();
        }
    };
    Handler<UnBroadcast> hUnBroadcast = new Handler<UnBroadcast>() {
        @Override
        public void handle(UnBroadcast e) {
            UnBroadcastMsg msg = new UnBroadcastMsg(myAddress, e.getUnDeliver());
            for (Address add : neighbors) {
                trigger(new Flp2pSend(add, msg), flp2pPort);
            }
        }
    };
    Handler<UnBroadcastMsg> hMessage = new Handler<UnBroadcastMsg>() {
        @Override
        public void handle(UnBroadcastMsg e) {
            trigger(e.getUnDeliver(), unPort);
        }
    };
}
