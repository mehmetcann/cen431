import simulation.SimulationEngine;

/**
 * CENG431 - Homework 1
 * Restaurant Order Coordination Simulation
 *
 * Entry point. No user input required — the simulation runs autonomously
 * for 10 ticks using probabilistic rules defined in the spec.
 */
public class Main {

    public static void main(String[] args) {
        SimulationEngine engine = new SimulationEngine();
        engine.run();
    }
}
