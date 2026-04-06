package colleague;

import event.EventType;
import mediator.Colleague;
import mediator.IMediator;
import mediator.RestaurantCoordinator;
import model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes payment for orders with a configurable failure probability (10%).
 * Payments are queued and processed in the next tick to ensure
 * PREPARATION_COMPLETED
 * state is visible for at least one tick.
 */
public class PaymentProcessor extends Colleague {

    private static final double PAYMENT_FAIL_PROB = 0.10;
    private final List<Order> pendingPayments;

    public PaymentProcessor(IMediator mediator) {
        super(mediator);
        this.pendingPayments = new ArrayList<>();
    }

    /**
     * Queues payment for the given order to be processed in the next tick.
     */
    public void processPayment(Order order) {
        pendingPayments.add(order);
    }

    @Override
    public void updateTick() {
        if (pendingPayments.isEmpty())
            return;
        List<Order> toProcess = new ArrayList<>(pendingPayments);
        pendingPayments.clear();
        for (Order order : toProcess) {
            if (((RestaurantCoordinator) mediator).getRandom().nextDouble() < PAYMENT_FAIL_PROB) {
                mediator.notify(this, EventType.PAYMENT_FAILED, order);
            } else {
                mediator.notify(this, EventType.PAYMENT_SUCCESS, order);
            }
        }
    }
}
