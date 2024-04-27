package connector.core;

/**
 * It defines the different levels of instrumentation. Each one of them is associated with an integer
 */
public enum InstrumentationLevels {
    /**
     * The instrumentation is skipped, so the code remains as the original one (more or less).
     * Integer 0.
     */
    NO_INSTRUMENTATION,
    /**
     * The instrumentation is run, but the event sending to the monitor is skipped.
     * Integer 1.
     */
    PARTIAL_INSTRUMENTATION,
    /**
     * The instrumentation is run and events are sent to the monitor.
     * Integer 2.
     */
    FULL_INSTRUMENTATION;

    public boolean isFull() {
        return this.compareTo(FULL_INSTRUMENTATION) == 0;
    }

    public boolean isPartial() {
        return this.compareTo(PARTIAL_INSTRUMENTATION) == 0;
    }

    public boolean isNoInstrumentation() {
        return this.compareTo(NO_INSTRUMENTATION) == 0;
    }
}
