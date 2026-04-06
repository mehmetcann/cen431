package mediator;

import colleague.*;
import event.EventType;
import model.Order;
import model.OrderState;
import model.PaymentState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Central mediator that routes all events between colleagues.
 * No colleague communicates directly with another — all interactions
 * pass through this coordinator.
 *
 * Implements the Mediator Design Pattern.
 */
public class RestaurantCoordinator implements IMediator {

    private final List<Order> allOrders;
    private double totalRevenue;
    private final Random random;

    private final OrderReceiver receiver;
    private final PreparationArea prepArea;
    private final List<ChefWorker> chefs;
    private final PaymentProcessor paymentProcessor;
    private final DeliveryWorker deliverer;

    public RestaurantCoordinator() {
        this.allOrders = new ArrayList<>();
        this.totalRevenue = 0.0;
        this.random = new Random();

        // Initialize all colleagues with this coordinator as mediator
        this.receiver = new OrderReceiver(this);
        this.prepArea = new PreparationArea(this);

        this.chefs = new ArrayList<>();
        this.chefs.add(new ChefWorker(this, 1));

        this.paymentProcessor = new PaymentProcessor(this);
        this.deliverer = new DeliveryWorker(this);
    }

    /**
     * Returns the first available (non-busy) chef, or null if all are occupied.
     */
    public ChefWorker getAvailableChef() {
        for (ChefWorker chef : chefs) {
            if (!chef.isBusy()) {
                return chef;
            }
        }
        return null;
    }

    @Override
    public void notify(Colleague sender, EventType event, Order order) {
        switch (event) {
            case EARLY_CANCELLATION:
                order.setCurrentState(OrderState.CANCELED);
                System.out.printf("  [x] %s early-canceled by customer%n", order.getId());
                break;

            case ORDER_CREATED:
                allOrders.add(order);
                System.out.printf("  [+] %s created ($%.2f, prep=%ds)%n",
                        order.getId(), order.getPrice(), order.getRemainingPrepTime());
                // Forward to preparation area
                prepArea.addOrder(order);
                break;

            case PREPARATION_STARTED:
                order.setCurrentState(OrderState.IN_PREPARATION);
                System.out.printf("  [~] %s preparation started (Chef %d)%n",
                        order.getId(), ((ChefWorker) sender).getChefId());
                break;

            case PREPARATION_DONE:
                order.setCurrentState(OrderState.PREPARATION_COMPLETED);
                System.out.printf("  [v] %s preparation completed%n", order.getId());
                // Immediately trigger payment
                paymentProcessor.processPayment(order);
                break;

            case PAYMENT_FAILED:
                order.setPaymentState(PaymentState.FAILED);
                order.setCurrentState(OrderState.CANCELED);
                System.out.printf("  [x] %s payment FAILED -> canceled%n", order.getId());
                break;

            case PAYMENT_SUCCESS:
                order.setPaymentState(PaymentState.SUCCESS);
                System.out.printf("  [$] %s payment successful%n", order.getId());
                deliverer.startDelivery(order);
                break;

            case DELIVERY_STARTED:
                System.out.printf("  [>] %s delivery started (est. %ds)%n",
                        order.getId(), order.getDeliveryDetails().getRemainingTime());
                break;

            case DELIVERY_DELAYED:
                System.out.printf("  [!] %s delivery DELAYED (+2s)%n", order.getId());
                break;

            case DELIVERY_COMPLETED:
                order.setCurrentState(OrderState.DELIVERED);
                addRevenue(order);
                System.out.printf("  [v] %s DELIVERED ($%.2f revenue)%n",
                        order.getId(), order.getPrice());
                break;

            default:
                break;
        }
    }

    /**
     * Adds the delivered order's price to total revenue.
     */
    public void addRevenue(Order order) {
        this.totalRevenue += order.getPrice();
    }

    /**
     * Polls the preparation area and assigns waiting orders to available chefs.
     */
    public void tryAssignChefs() {
        while (prepArea.hasWaitingOrder()) {
            ChefWorker chef = getAvailableChef();
            if (chef == null) {
                break; // All chefs busy
            }
            Order order = prepArea.pollNextOrder();
            if (order != null) {
                chef.startPreparation(order);
            }
        }
    }

    /**
     * Advances all components by one tick.
     */
    public void tickAll() {
        receiver.updateTick();
        tryAssignChefs();
        for (ChefWorker chef : chefs) {
            chef.updateTick();
        }
        tryAssignChefs();
        deliverer.updateTick();
    }

    @Override
    public void printStatusReport() {
        int[] counts = new int[OrderState.values().length];
        int delayedCount = 0;

        for (Order o : allOrders) {
            counts[o.getCurrentState().ordinal()]++;
            if (o.getDeliveryDetails().isDelayed()) {
                delayedCount++;
            }
        }

        System.out.println();
        System.out.println("=======================================================");
        System.out.println("  PERIODIC STATUS REPORT");
        System.out.println("=======================================================");
        System.out.printf("  Total orders created      : %d%n", allOrders.size());
        System.out.printf("  Currently in preparation  : %d%n", counts[OrderState.IN_PREPARATION.ordinal()]);
        System.out.printf("  Preparation completed     : %d%n", counts[OrderState.PREPARATION_COMPLETED.ordinal()]);
        System.out.printf("  Currently in delivery     : %d%n", counts[OrderState.IN_DELIVERY.ordinal()]);
        System.out.printf("  Delivered                 : %d%n", counts[OrderState.DELIVERED.ordinal()]);
        System.out.printf("  Canceled                  : %d%n", counts[OrderState.CANCELED.ordinal()]);
        System.out.printf("  Delayed deliveries        : %d%n", delayedCount);
        System.out.printf("  Total revenue             : $%.2f%n", totalRevenue);
        System.out.println("=======================================================");
        System.out.println();
    }

    @Override
    public void printFinalReport() {
        int[] counts = new int[OrderState.values().length];
        int delayedCount = 0;
        int inProgress = 0;

        for (Order o : allOrders) {
            OrderState state = o.getCurrentState();
            counts[state.ordinal()]++;
            if (o.getDeliveryDetails().isDelayed()) {
                delayedCount++;
            }
            if (state == OrderState.IN_PREPARATION
                    || state == OrderState.PREPARATION_COMPLETED
                    || state == OrderState.IN_DELIVERY) {
                inProgress++;
            }
        }

        System.out.println();
        System.out.println("#######################################################");
        System.out.println("  FINAL SIMULATION REPORT");
        System.out.println("#######################################################");
        System.out.printf("  Total orders created         : %d%n", allOrders.size());
        System.out.printf("  Canceled orders              : %d%n", counts[OrderState.CANCELED.ordinal()]);
        System.out.printf("  Delayed deliveries           : %d%n", delayedCount);
        System.out.printf("  Successfully delivered       : %d%n", counts[OrderState.DELIVERED.ordinal()]);
        System.out.printf("  Still in progress (end)      : %d%n", inProgress);
        System.out.printf("  Total revenue                : $%.2f%n", totalRevenue);
        System.out.println("#######################################################");
    }

    // ── Getters for SimulationEngine ──

    public Random getRandom() {
        return random;
    }

    public OrderReceiver getReceiver() {
        return receiver;
    }

    public PreparationArea getPrepArea() {
        return prepArea;
    }

    public List<ChefWorker> getChefs() {
        return chefs;
    }

    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public DeliveryWorker getDeliverer() {
        return deliverer;
    }

    public List<Order> getAllOrders() {
        return allOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
