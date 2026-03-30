package mediator;

/**
 * Abstract base class for all system components (colleagues) in the Mediator pattern.
 * Each colleague holds a reference to the mediator and never communicates
 * with other colleagues directly.
 */
public abstract class Colleague {

    protected IMediator mediator;

    public Colleague(IMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Called every simulation tick to advance this colleague's internal state.
     */
    public abstract void updateTick();
}
