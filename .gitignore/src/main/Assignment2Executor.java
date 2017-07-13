/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

/**
 *
 * @author M&M
 */
public class Assignment2Executor {
    private static final int NODES = 6;

    public static void main(String[] args) {
        Topology topology1 = new Topology() {
            {
                node(1, "127.0.0.1", 10001);
                node(2, "127.0.0.1", 10002);
                node(3, "127.0.0.1", 10003);
                node(4, "127.0.0.1", 10004);

                //link(1, 2, 500, 0.5).bidirectional();
                // link(1, 2, 3000, 0.5);
                // link(2, 1, 3000, 0.5);
                // link(3, 2, 3000, 0.5);
                // link(4, 2, 3000, 0.5);
                defaultLinks(500, 0.7);
            }
        };
        Topology topology2 = new Topology() {
            {
                for (int i = 1; i <= NODES; i++) {
                    node(i, "127.0.0.1", 22220 + i);
                }
//                link(1, 2, 100, 0.5).bidirectional();
//                link(2, 3, 100, 0.5).bidirectional();
//                defaultLinks(100, 0.0);
                defaultLinks(100, 0.4);
            }
        };
        Scenario scenario1 = new Scenario(Assignment2Main.class) {
            {
                command(1, "S500");
                command(2, "S500");
                command(3, "S500");
                command(4, "S500");
            }
        };
        Scenario scenario2 = new Scenario(Assignment2Main.class) {
            {
                command(1, "S500:BHello from 1");
                command(2, "S10500:BHello from 2");
                command(3, "S20500:BHello from 3");
                command(4, "S30500:BHello from 4");
            }
        };
        Scenario scenario3 = new Scenario(Assignment2Main.class) {
            {
                command(1, "S500:Bdebug1");
                command(2, "S2000:X");
                command(3, "S2000:X");
                command(4, "S2000:X");
            }
        };
        Scenario scenario4 = new Scenario(Assignment2Main.class) {
            {
                command(1, "S100:Ba1:"
                        + "S200:Ba2:"
                        + "S200:Ba3:"
                        + "S200:Ba4:"
                        + "S200:Ba5:"
                        + "S200:Ba6:"
                        + "S200:Ba7:"
                        + "S200:Ba8:"
                        + "S200:Ba9:"
                        + "S200:Ba10");
                command(2, "S100");
                command(3, "S150:Bb1:"
                        + "S250:Bb2:"
                        + "S250:Bb3:"
                        + "S250:Bb4:"
                        + "S250:Bb5:"
                        + "S250:Bb6:"
                        + "S250:Bb7:"
                        + "S250:Bb8:"
                        + "S250:Bb9:"
                        + "S250:Bb10");
                for (int i = 4; i <= NODES; i++) {
                    command(i, "S100");
                }
            }
        };
        Scenario scenario5 = new Scenario(Assignment2Main.class) {
            {
                command(1, "S500:Bm1:S500:Bm2");
                command(2, "S500");
                command(3, "S500");
                command(4, "S500");
                command(5, "S500");
                command(6, "S500");
            }
        };

        scenario5.executeOn(topology2);

        System.exit(0);
    }
}

