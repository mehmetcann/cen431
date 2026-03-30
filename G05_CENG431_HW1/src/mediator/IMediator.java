package mediator;

import event.EventType;
import model.Order;

/**
 * Mediator interface defining the central communication contract.
 * All colleagues communicate exclusively through this interface.
 */
public interface IMediator {

    /**
     * Receives an event notification from a colleague and routes it appropriately.
     *
     * @param sender the colleague that triggered the event
     * @param event  the type of event
     * @param order  the order associated with the event
     */
    void notify(Colleague sender, EventType event, Order order);

    /**
     * Prints the current periodic status report.
     */
    void printStatusReport();

    /**
     * Prints the final simulation summary report.
     */
    void printFinalReport();
}
