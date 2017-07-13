
package pbbroadcast.port;

import se.sics.kompics.PortType;

/**
 *
 * @author M&M
 */
public class PbBroadcastPort extends PortType {
    {
        indication(PbDeliver.class);
        request(PbBroadcast.class);
    }
}
