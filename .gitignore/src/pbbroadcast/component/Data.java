
package pbbroadcast.component;

import id2203.link.flp2p.Flp2pDeliver;
import pbbroadcast.port.PbDeliver;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public class Data extends Flp2pDeliver implements Comparable<Data> {
    private PbDeliver pbDeliver;
    private int sn;

    public Data(Address src, PbDeliver pbDeliver, int sn) {
        super(src);
        this.pbDeliver = pbDeliver;
        this.sn = sn;
    }

    public PbDeliver getPbDeliver() {
        return pbDeliver;
    }

    public int getSn() {
        return sn;
    }

    @Override
    public int hashCode() {
        int h = 0;
        h = h + (this.getSource() != null ? this.getSource().hashCode() : 0);
        h = h + (this.pbDeliver != null ? this.pbDeliver.hashCode() : 0);
        h = h + this.sn;
        return h;
    }

    @Override
    public boolean equals(Object o) {
        final Data dm = (Data) o;
        if ((this.getSource() == null) ? (dm.getSource() != null) : !this.getSource().equals(dm.getSource())) {
            return false;
        } 
        if (getClass() != o.getClass()) {
            return false;
        }
        if (this.sn != dm.sn) {
            return false;
        }
        if (o == null) {
            return false;
        }  
        if ((this.pbDeliver == null) ? (dm.pbDeliver != null) : !this.pbDeliver.equals(dm.pbDeliver)) {
            return false;
        }
        return true;
    }
    @Override
    public int compareTo(Data obj) {
        return this.getSource().equals(obj.getSource()) ? Integer.compare(sn, obj.sn): 0;
    }
}

