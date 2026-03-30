package simulation;

import colleague.ChefWorker;
import mediator.RestaurantCoordinator;

/**
 * Drives the simulation for a configurable number of ticks (default: 10).
 * Each tick: generates orders, assigns chefs, advances preparation,
 * advances delivery. Prints status every 2 ticks.
 */
public class SimulationEngine {

    private final RestaurantCoordinator coordinator;
    private int currentTick;
    private final int totalTicks;

    public SimulationEngine() {
        this(10);
    }

    public SimulationEngine(int totalTicks) {
        this.coordinator = new RestaurantCoordinator();
        this.currentTick = 0;
        this.totalTicks = totalTicks;
    }

    /**
     * Advances the simulation by one tick.
     * Order of operations per tick:
     * 1. Generate new orders (OrderReceiver)
     * 2. Assign queued orders to available chefs
     * 3. Chefs work (tick preparation)
     * 4. Re-assign (a chef may have just freed up)
     * 5. Deliveries tick
     * 6. Print status every 2 ticks
     */
    public void tick() {
        currentTick++;
        System.out.printf("%n--- Tick %d ---%n", currentTick);

        // 1. Generate new orders
        coordinator.getReceiver().updateTick();

        // 2. Assign waiting orders to available chefs
        coordinator.tryAssignChefs();

        // 3. Chefs advance their current preparation
        for (ChefWorker chef : coordinator.getChefs()) {
            chef.updateTick();
        }

        // 4. Re-try assignment (chef may have finished above)
        coordinator.tryAssignChefs();

        // 5. Deliveries advance
        coordinator.getDeliverer().updateTick();

        // 6. Periodic status report every 2 ticks
        if (currentTick % 2 == 0) {
            coordinator.printStatusReport();
        }
    }

    /**
     * Runs the full simulation.
     */
    public void run() {
        System.out.println("Restaurant Order Coordination Simulation");
        System.out.printf("Duration: %d seconds | 1 chef | Mediator pattern%n", totalTicks);

        for (int i = 0; i < totalTicks; i++) {
            try {
                Thread.sleep(1000); // Real 1-second delay between ticks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulation interrupted.");
                return;
            }
            tick();
        }

        coordinator.printFinalReport();
    }
}
