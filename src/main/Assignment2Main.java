
package main;

import application.Application;
import application.ApplicationInit;
import id2203.link.flp2p.FairLossPointToPointLink;
import id2203.link.flp2p.delay.DelayDropLink;
import id2203.link.flp2p.delay.DelayDropLinkInit;
import java.util.Set;
import org.apache.log4j.PropertyConfigurator;
import pbbroadcast.component.LazyPbBroadcast;
import pbbroadcast.component.LazyPbBroadcastInit;
import pbbroadcast.port.PbBroadcastPort;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import unbroadcast.component.UnBroadcastInit;
import unbroadcast.component.UnBroadcastComponent;
import unbroadcast.port.UnBroadcastPort;

/**
 *
 * @author M&M
 */
public class Assignment2Main extends ComponentDefinition {
    static {
        PropertyConfigurator.configureAndWatch("log4j.properties");
    }
    private static int fanout = 3;
    private static int maxrounds = 2;
    private static float store_threshold = 0.7f;
    private static int delta = 5000;
    private static int myId;
    private static String commandScript;
    Topology topology = Topology.load(System.getProperty("topology"), myId);

    public static void main(String[] args) {
        myId = Integer.parseInt(args[0]);
        commandScript = args[1];

        Kompics.createAndStart(Assignment2Main.class);
    }

    public Assignment2Main() {

        Component application = create(Application.class);
        Component lPB = create(LazyPbBroadcast.class);
        Component sUB = create(UnBroadcastComponent.class);
        Component flp2p = create(DelayDropLink.class);
        Component minaNetwork = create(MinaNetwork.class);
        Component timer = create(JavaTimer.class);

        subscribe(handelFault, application.control());
        subscribe(handelFault, lPB.control());
        subscribe(handelFault, sUB.control());
        subscribe(handelFault, flp2p.control());
        subscribe(handelFault, minaNetwork.control());
        subscribe(handelFault, timer.control());

        Address myAddress = topology.getSelfAddress();
        Set<Address> neighbors = topology.getNeighbors(myAddress);

        trigger(new ApplicationInit(commandScript, myAddress), application.control());
        trigger(new LazyPbBroadcastInit(neighbors, myAddress, System.nanoTime(), fanout, store_threshold, delta, maxrounds), lPB.control());
        trigger(new UnBroadcastInit(neighbors, myAddress), sUB.control());
        trigger(new DelayDropLinkInit(topology, System.nanoTime()), flp2p.control());
        trigger(new MinaNetworkInit(myAddress, 5), minaNetwork.control());
       
        connect(application.required(PbBroadcastPort.class), lPB.provided(PbBroadcastPort.class));
        connect(application.required(Timer.class), timer.provided(Timer.class));

        connect(lPB.required(UnBroadcastPort.class), sUB.provided(UnBroadcastPort.class));
        connect(lPB.required(FairLossPointToPointLink.class), flp2p.provided(FairLossPointToPointLink.class));
        connect(lPB.required(Timer.class), timer.provided(Timer.class));

        connect(sUB.required(FairLossPointToPointLink.class), flp2p.provided(FairLossPointToPointLink.class));

        connect(flp2p.required(Network.class), minaNetwork.provided(Network.class));
        connect(flp2p.required(Timer.class), timer.provided(Timer.class));
    }
    
    Handler<Fault> handelFault = new Handler<Fault>() {
        @Override
        public void handle(Fault f) {
            f.getFault().printStackTrace(System.err);
        }
    };
}

