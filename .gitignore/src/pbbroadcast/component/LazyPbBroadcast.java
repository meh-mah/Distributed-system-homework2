
package pbbroadcast.component;

import id2203.link.flp2p.FairLossPointToPointLink;
import id2203.link.flp2p.Flp2pSend;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pbbroadcast.port.PbBroadcast;
import pbbroadcast.port.PbBroadcastPort;
import pbbroadcast.port.PbDeliver;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import unbroadcast.port.UnBroadcast;
import unbroadcast.port.UnBroadcastPort;

/**
 *
 * @author M&M
 */
public class LazyPbBroadcast extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(LazyPbBroadcast.class);
    
    Negative<PbBroadcastPort> pbPort = provides(PbBroadcastPort.class);
    
    Positive<UnBroadcastPort> unBPort = requires(UnBroadcastPort.class);
    Positive<FairLossPointToPointLink> flp2pPort = requires(FairLossPointToPointLink.class);
    Positive<Timer> timerPort = requires(Timer.class);

    private Address myAddress;
    private Set<Address> neighbors;
    private Map<Address, Integer> nextSn;
    private int sn;
    private Set<Data> pending = new TreeSet<>();
    private Set<Data> saved = new HashSet<>();
    private Random r;
    private long s;
    private int maxrounds;
    private int delta;
    private int fanout;
    private float store_threshold;

    public LazyPbBroadcast() {
        subscribe(hInit, control);
        subscribe(hPbBroadcast, pbPort);
        subscribe(HlazyPbBroadcastMsg, unBPort);
        subscribe(hRequest, flp2pPort);
        subscribe(hDataMessage, flp2pPort);
        subscribe(hTimeout, timerPort);
    }

    Handler<LazyPbBroadcastInit> hInit = new Handler<LazyPbBroadcastInit>() {
        @Override
        public void handle(LazyPbBroadcastInit e) {
            neighbors = e.getNeighbors();
            myAddress = e.getMyAddress();
            s = e.getSeed();
            r = new Random(s);
            maxrounds = e.getMaxRound();
            fanout = e.getFanout();
            store_threshold = e.getStoreThreshold();
            delta = e.getDelta();
            nextSn = new HashMap<>();
            for (Address add : neighbors) {
                nextSn.put(add, 1);
            }
            nextSn.put(myAddress, 1);
            sn = 0;
        }
    };
    Handler<PbBroadcast> hPbBroadcast = new Handler<PbBroadcast>() {
        @Override
        public void handle(PbBroadcast event) {
            ++sn;
            trigger(new UnBroadcast(new LazyPbMsg(myAddress, event.getPbDeliver(), sn)), unBPort);
        }
    };
    Handler<LazyPbMsg> HlazyPbBroadcastMsg = new Handler<LazyPbMsg>() {
        @Override
        public void handle(LazyPbMsg e) {
            Address source = e.getSrc();
            int sequenceNumber = e.getSn();
            PbDeliver msg = e.getPbDeliver();

            Data dm = new Data(source, msg, sequenceNumber);
            if (r.nextFloat() < store_threshold) {
                logger.debug(" storing message: sn= '{}'  source= '{}' stored", sequenceNumber, source);
                saved.add(dm);
            }

            if (sequenceNumber == nextSn.get(source)) {
                nextSn.put(source, nextSn.remove(source) + 1);
                trigger(msg, pbPort);
            } else if (sequenceNumber > nextSn.get(source)) {
                logger.info("recived message number {}, but waiting for {}", sequenceNumber, nextSn.get(source));
                logger.info("Missing messages from number {} to number {}", nextSn.get(source),  sequenceNumber - 1);
                pending.add(dm);
                //starting to look for missed messages
                for (int missed = nextSn.get(source); missed < sequenceNumber; missed++) {
                    boolean messageFound = false;
                    for (Data dmsg : pending) {
                        if ((source.equals(dmsg.getSource())) && (missed == dmsg.getSn())) {
                            messageFound = true;
                            break;
                        }
                    }
                    if (!messageFound) {
                        gossip(new Request(myAddress, source, missed, maxrounds - 1));
                    }
                }

                ScheduleTimeout stimeout = new ScheduleTimeout(delta);
                stimeout.setTimeoutEvent(new LPBPullTimeout(stimeout, source, sequenceNumber));
                trigger(stimeout, timerPort);
            }
        }
    };
    Handler<Request> hRequest = new Handler<Request>() {
        @Override
        public void handle(Request e) {
            Address source = e.getSource();
            Address bSource = e.getBSource();
            int sequenceNumber = e.getSn();

            Data missedMsg = null;
            for (Data dmsg : saved) {
                if ((bSource.equals(dmsg.getSource()))&& (sequenceNumber == dmsg.getSn())) {
                    missedMsg = dmsg;
                    break;
                }
            }
            if (missedMsg != null) {
                trigger(new Flp2pSend(source, missedMsg), flp2pPort);
            } else if (e.maxround > 0) {
                --e.maxround;
                gossip(e);
            }
                
        }
    };
    Handler<Data> hDataMessage = new Handler<Data>() {
        @Override
        public void handle(Data e) {
            if (e.getSn() < nextSn.get(e.getSource())) {
                return;
            }
            pending.add(e);
            pending();
        }
    };
    Handler<LPBPullTimeout> hTimeout = new Handler<LPBPullTimeout>() {
        @Override
        public void handle(LPBPullTimeout e) {
            Address bSource = e.getBSource();
            int sequenceNumber = e.getSn();

            if (sequenceNumber > nextSn.get(bSource)) {
                logger.info("missed messages number {} to {} from {} could not be retrived and Skipped", new Object[]{nextSn.get(bSource), sequenceNumber - 1, bSource});
                nextSn.remove(bSource);
                nextSn.put(bSource, sequenceNumber + 1);
                deliver(bSource, sequenceNumber);
            }
        }
    };

    private void pending() {
        boolean Next;
        do {
            Next = false;
            Iterator<Data> j = pending.iterator();
            while (j.hasNext()) {
                Data dmsg = j.next();
                int sequenceNumber = dmsg.getSn();
                PbDeliver msg = dmsg.getPbDeliver();
                Address src = dmsg.getSource();
                if (sequenceNumber == nextSn.get(src)) {
                    nextSn.put(src, nextSn.remove(src) + 1);
                    j.remove();
                    trigger(msg, pbPort);
                    Next = true;
                }
            }
        } while (Next);
    }
    private Set<Address> selectGossipDes(int fanout) {

        if (fanout > neighbors.size()) {
            return new HashSet<>(neighbors);
        }

        Set<Address> c = new HashSet<>(neighbors);
        c.remove(myAddress);
        Set<Address> destination = new HashSet<>();
        while (destination.size() < fanout) {
            Address add = (Address) c.toArray()[r.nextInt(c.size())];
            c.remove(add);
            destination.add(add);
        }

        return destination;
    }

    private void gossip(Request r) {
        for (Address add : selectGossipDes(fanout)) {
            trigger(new Flp2pSend(add, r), flp2pPort);
        }
    }


    private void deliver(Address src, int sequenceNumber) {
        Iterator<Data> j = pending.iterator();
        while (j.hasNext()) {
            Data dmsg = j.next();
            PbDeliver message = dmsg.getPbDeliver();
            int sNumber;
            sNumber = dmsg.getSn();
            Address sAdd;
            sAdd = dmsg.getSource();

            if (sAdd.equals(src) && (sNumber <= sequenceNumber)) {
                j.remove();
                trigger(message, pbPort);
            }
        }
    }
}
