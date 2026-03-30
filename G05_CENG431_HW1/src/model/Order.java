package model;

public class Order {

    private static int counter = 0;

    private final String id;
    private final double price;
    private OrderState currentState;
    private PaymentState paymentState;
    private final int preparationTime;
    private int remainingPrepTime;
    private final DeliveryDetails deliveryDetails;

    public Order(double price, int prepTime, int delivTime) {
        counter++;
        this.id = "ORD-" + String.format("%03d", counter);
        this.price = price;
        this.currentState = OrderState.RECEIVED;
        this.paymentState = PaymentState.PENDING;
        this.preparationTime = prepTime;
        this.remainingPrepTime = prepTime;
        this.deliveryDetails = new DeliveryDetails(delivTime);
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public OrderState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(OrderState currentState) {
        this.currentState = currentState;
    }

    public PaymentState getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public int getRemainingPrepTime() {
        return remainingPrepTime;
    }

    public void tickPreparation() {
        if (remainingPrepTime > 0) {
            remainingPrepTime--;
        }
    }

    public boolean isPreparationDone() {
        return remainingPrepTime <= 0;
    }

    /**
     * Resets the static counter. Useful for testing.
     */
    public static void resetCounter() {
        counter = 0;
    }

    @Override
    public String toString() {
        return String.format("Order(%s, price=%.2f, state=%s, payment=%s)",
                id, price, currentState, paymentState);
    }
}
