
package application;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 *
 * @author M&M
 */
public class ApplicationInit extends Init {
    private String command;
    private Address myAddress;

    public ApplicationInit(String com) {
        this.command = com;
    }

    public ApplicationInit(String commandScript, Address self) {
        this(commandScript);
        this.myAddress = self;
    }

    public String getCommand() {
        return command;
    }

    public Address getMyAddress() {
        return myAddress;
    }
}
