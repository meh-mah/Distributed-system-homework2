
package unbroadcast.component;

import java.util.Set;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public class UnBroadcastInit extends Init {
    private Address myAddress;
    private Set<Address> neighbors;

    public UnBroadcastInit(Set<Address> neighbors, Address myAddress) {
        this.myAddress = myAddress;
        this.neighbors = neighbors;
        
    }
    
    public Address getMyAddress() {
        return myAddress;
    }
    
    public Set<Address> getNeighbors() {
        return neighbors;
    }

    
}
