package connector.core;

public enum RMLOperations {
    SEND("send"),
    SUBSCRIPTION("subscription"),
    RECEIVE("receive"),

    NEW("new"),
    ;

    private final String value;

    RMLOperations(final String str) {
        value = str;
    }

    @Override
    public String toString() {
        return value;
    }
}
