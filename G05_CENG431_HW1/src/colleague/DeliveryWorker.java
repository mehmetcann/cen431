package colleague;

import event.EventType;
import mediator.Colleague;
import mediator.IMediator;
import model.DeliveryDetails;
import model.DeliveryState;
import model.Order;
import model.OrderState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Manages multiple concurrent deliveries. Each delivery ticks independently.
 * Delays are determined at the start of delivery (p=0.15, +2 seconds).
 */
public class DeliveryWorker extends Colleague {

    private static final double DELAY_PROB = 0.15;

    private final List<Order> deliveringOrders;
    private final Random random;

    public DeliveryWorker(IMediator mediator) {
        super(mediator);
        this.deliveringOrders = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Starts delivery for an order. Determines delay at assignment time.
     */
    public void startDelivery(Order order) {
        DeliveryDetails details = order.getDeliveryDetails();
        details.setDeliveryState(DeliveryState.IN_PROGRESS);

        // Determine delay at delivery start
        if (random.nextDouble() < DELAY_PROB) {
            details.applyDelay();
            mediator.notify(this, EventType.DELIVERY_DELAYED, order);
        }

        order.setCurrentState(OrderState.IN_DELIVERY);
        deliveringOrders.add(order);
        mediator.notify(this, EventType.DELIVERY_STARTED, order);
    }

    @Override
    public void updateTick() {
        List<Order> completed = new ArrayList<>();

        for (Order order : deliveringOrders) {
            DeliveryDetails details = order.getDeliveryDetails();
            details.tick();

            if (details.isDone()) {
                details.setDeliveryState(DeliveryState.COMPLETED);
                completed.add(order);
            }
        }

        for (Order order : completed) {
            deliveringOrders.remove(order);
            mediator.notify(this, EventType.DELIVERY_COMPLETED, order);
        }
    }

    /**
     * Returns the number of orders currently being delivered.
     */
    public int getDeliveringCount() {
        return deliveringOrders.size();
    }
}
