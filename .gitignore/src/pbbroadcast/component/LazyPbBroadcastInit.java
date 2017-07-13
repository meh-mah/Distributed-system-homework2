
package pbbroadcast.component;

import java.util.Set;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public class LazyPbBroadcastInit extends Init {
    private Set<Address> neighbors;
    private Address myAddress;
    private long s;
    private int delta;
    private int max_rounds;
    private int fanout;
    private float store_threshold;
    
    public LazyPbBroadcastInit(Set<Address> neighbors, Address myAddress, long s, int fanout, float store_threshold, int delta, int max_rounds) {
        this.neighbors = neighbors;
        this.myAddress = myAddress;
        this.s = s;
        this.delta = delta;
        this.max_rounds = max_rounds;
        this.fanout = fanout;
        this.store_threshold = store_threshold;    
    }

    public Set<Address> getNeighbors() {
        return neighbors;
    }

    public Address getMyAddress() {
        return myAddress;
    }

    public float getStoreThreshold() {
        return store_threshold;
    } 
    
    public long getSeed() {
        return s;
    }

    public int getDelta() {
        return delta;
    }

    public int getMaxRound() {
        return max_rounds;
    }  
    
    public int getFanout() {
        return fanout;
    }
}

