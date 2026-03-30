package model;

public class DeliveryDetails {

    private DeliveryState deliveryState;
    private int deliveryTime;
    private int remainingTime;
    private boolean delayed;

    public DeliveryDetails(int deliveryTime) {
        this.deliveryState = DeliveryState.NOT_STARTED;
        this.deliveryTime = deliveryTime;
        this.remainingTime = deliveryTime;
        this.delayed = false;
    }

    public void tick() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public void applyDelay() {
        this.delayed = true;
        this.remainingTime += 2;
        this.deliveryState = DeliveryState.DELAYED;
    }

    public boolean isDone() {
        return remainingTime <= 0;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public DeliveryState getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }
}
