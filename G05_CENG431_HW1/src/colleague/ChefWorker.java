package colleague;

import event.EventType;
import mediator.Colleague;
import mediator.IMediator;
import model.Order;

/**
 * Worker responsible for physically preparing a single order at a time.
 * Decrements remaining preparation time each tick and notifies the mediator
 * when preparation is complete.
 */
public class ChefWorker extends Colleague {

    private final int chefId;
    private Order currentOrder;
    private boolean busy;

    public ChefWorker(IMediator mediator, int chefId) {
        super(mediator);
        this.chefId = chefId;
        this.currentOrder = null;
        this.busy = false;
    }

    /**
     * Assigns an order to this chef and begins preparation.
     */
    public void startPreparation(Order order) {
        this.currentOrder = order;
        this.busy = true;
        mediator.notify(this, EventType.PREPARATION_STARTED, order);
    }

    public boolean isBusy() {
        return busy;
    }

    public int getChefId() {
        return chefId;
    }

    @Override
    public void updateTick() {
        if (!busy || currentOrder == null) {
            return;
        }

        currentOrder.tickPreparation();

        if (currentOrder.isPreparationDone()) {
            Order finishedOrder = currentOrder;
            this.currentOrder = null;
            this.busy = false;
            mediator.notify(this, EventType.PREPARATION_DONE, finishedOrder);
        }
    }
}
