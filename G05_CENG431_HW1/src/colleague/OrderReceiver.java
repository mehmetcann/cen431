package colleague;

import event.EventType;
import mediator.Colleague;
import mediator.IMediator;
import mediator.RestaurantCoordinator;
import model.Order;

import java.util.Random;

/**
 * Responsible for generating new orders probabilistically each tick.
 * Also handles early cancellation decisions.
 */
public class OrderReceiver extends Colleague {

    private static final double ORDER_CREATION_PROB = 0.65;
    private static final double EARLY_CANCEL_PROB = 0.08;

    public OrderReceiver(IMediator mediator) {
        super(mediator);
    }

    /**
     * Creates a new order with random price (50-200), prep time (1-4s),
     * and delivery time (1-4s).
     */
    public Order createOrder() {
        Random random = ((RestaurantCoordinator) mediator).getRandom();
        double price = 50 + random.nextDouble() * 150; // [50, 200)
        int prepTime = 1 + random.nextInt(4); // [1, 4]
        int delivTime = 1 + random.nextInt(4); // [1, 4]
        return new Order(price, prepTime, delivTime);
    }

    /**
     * Determines whether an order should be early-canceled (p=0.08).
     *
     * @param order the order to evaluate
     * @return true if the order is canceled
     */
    public boolean decideEarlyCancellation(Order order) {
        return ((RestaurantCoordinator) mediator).getRandom().nextDouble() < EARLY_CANCEL_PROB;
    }

    @Override
    public void updateTick() {
        if (((RestaurantCoordinator) mediator).getRandom().nextDouble() < ORDER_CREATION_PROB) {
            Order order = createOrder();
            if (decideEarlyCancellation(order)) {
                mediator.notify(this, EventType.EARLY_CANCELLATION, order);
            } else {
                mediator.notify(this, EventType.ORDER_CREATED, order);
            }
        }
    }
}
