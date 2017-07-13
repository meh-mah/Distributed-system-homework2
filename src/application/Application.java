
package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pbbroadcast.port.PbBroadcast;
import pbbroadcast.port.PbBroadcastPort;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

/**
 *
 * @author M&M
 */
public final class Application extends ComponentDefinition {

    Positive<PbBroadcastPort> pbPort = requires(PbBroadcastPort.class);
    Positive<Timer> timerPort = requires(Timer.class);

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private String[] commands;
    private int lastCommand;
    private Address myAddress;

    public Application() {
        subscribe(hInit, control);
        subscribe(hStart, control);
        subscribe(hContinue, timerPort);
        subscribe(hMessage, pbPort);
    }

    Handler<ApplicationInit> hInit = new Handler<ApplicationInit>() {
        @Override
        public void handle(ApplicationInit event) {
            commands = event.getCommand().split(":");
            myAddress = event.getMyAddress();
            lastCommand = -1;
        }
    };
    Handler<Start> hStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            doNextCommand();
        }
    };
    Handler<ApplicationContinue> hContinue = new Handler<ApplicationContinue>() {
        @Override
        public void handle(ApplicationContinue event) {
            doNextCommand();
        }
    };
    Handler<PbDeliverMessage> hMessage = new Handler<PbDeliverMessage>() {
        @Override
        public void handle(PbDeliverMessage event) {
            logger.info("Message '{}' received from {}", event.getMessage(), event.getSrc());
        }
    };

    private void doNextCommand() {
        lastCommand++;

        if (lastCommand > commands.length) {
            return;
        }
        if (lastCommand == commands.length) {
            logger.info("DONE ALL OPERATIONS");
            Thread applicationThread = new Thread("ApplicationThread") {
                @Override
                public void run() {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(System.in));
                    while (true) {
                        try {
                            String line = in.readLine();
                            doCommand(line);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            applicationThread.start();
            return;
        }
        String op = commands[lastCommand];
        doCommand(op);
    }

    private void doCommand(String cmd) {
        if (cmd.startsWith("S")) {
            doSleep(Integer.parseInt(cmd.substring(1)));
        } else if (cmd.startsWith("X")) {
            doShutdown();
        } else if (cmd.equals("help")) {
            doHelp();
            doNextCommand();
        } else if (cmd.startsWith("B")) {
            doBroadcast(cmd.substring(1));
            doNextCommand();
        } else {
            logger.info("Bad command: '{}'. Try 'help'", cmd);
            doNextCommand();
        }
    }

    private void doHelp() {
        logger.info("Available commands: help, S<n>, X, B<m>");
        logger.info("help: shows this help message");
        logger.info("Sn: sleeps 'n' milliseconds before the next command");
        logger.info("X: terminates this process");
        logger.info("Bm: unreliable broadcasts message 'm'");
    }

    private void doSleep(long delay) {
        logger.info("Sleeping {} milliseconds...", delay);

        ScheduleTimeout st = new ScheduleTimeout(delay);
        st.setTimeoutEvent(new ApplicationContinue(st));
        trigger(st, timerPort);
    }

    private void doShutdown() {
        System.out.println("2DIE");
        System.out.close();
        System.err.close();
        Kompics.shutdown();
    }

    private void doBroadcast(String message) {
        logger.info("message ::{}:: Broadcasted ", message);
        trigger(new PbBroadcast(new PbDeliverMessage(myAddress, message)), pbPort);
    }
}
