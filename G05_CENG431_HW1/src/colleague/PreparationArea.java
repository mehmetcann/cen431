package colleague;

import mediator.Colleague;
import mediator.IMediator;
import model.Order;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue-based holding area for orders awaiting chef assignment.
 * Manages the waiting queue; actual preparation is done by ChefWorker.
 */
public class PreparationArea extends Colleague {

    private final Queue<Order> waitingOrders;

    public PreparationArea(IMediator mediator) {
        super(mediator);
        this.waitingOrders = new LinkedList<>();
    }

    /**
     * Adds an order to the preparation queue.
     */
    public void addOrder(Order order) {
        waitingOrders.add(order);
    }

    /**
     * Removes and returns the next order from the queue, or null if empty.
     */
    public Order pollNextOrder() {
        return waitingOrders.poll();
    }

    /**
     * Checks if there are orders waiting for preparation.
     */
    public boolean hasWaitingOrder() {
        return !waitingOrders.isEmpty();
    }

    /**
     * Returns the number of orders currently in the waiting queue.
     */
    public int getWaitingCount() {
        return waitingOrders.size();
    }

    @Override
    public void updateTick() {
        // Assignment is driven by the coordinator to maintain mediator pattern
    }
}
