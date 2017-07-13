
package unbroadcast.port;

import se.sics.kompics.PortType;

/**
 *
 * @author M&M
 */
public class UnBroadcastPort extends PortType {
    {
        indication(UnDeliver.class);
        request(UnBroadcast.class);
    }
}

