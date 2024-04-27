package connector.core;

public enum RMLAgents {
    PUBLISHER("pub"),
    SUBSCRIBER("sub"),
    ;

    private final String value;

    RMLAgents(final String str) {
        value = str;
    }

    @Override
    public String toString() {
        return value;
    }
}
