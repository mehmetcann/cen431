package colleague;

import event.EventType;
import mediator.Colleague;
import mediator.IMediator;
import model.Order;

import java.util.Random;

/**
 * Processes payment for orders with a configurable failure probability (10%).
 * Payment is instantaneous and triggered by the mediator after preparation completes.
 */
public class PaymentProcessor extends Colleague {

    private static final double PAYMENT_FAIL_PROB = 0.10;

    private final Random random;

    public PaymentProcessor(IMediator mediator) {
        super(mediator);
        this.random = new Random();
    }

    /**
     * Processes payment for the given order.
     * Notifies the mediator of success or failure.
     */
    public void processPayment(Order order) {
        if (random.nextDouble() < PAYMENT_FAIL_PROB) {
            mediator.notify(this, EventType.PAYMENT_FAILED, order);
        } else {
            mediator.notify(this, EventType.PAYMENT_SUCCESS, order);
        }
    }

    @Override
    public void updateTick() {
        // Payment is event-driven, not tick-driven
    }
}
