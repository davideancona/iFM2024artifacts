import connector.RMLConnector;
import connector.RMLConnectorImplementation;
import org.junit.jupiter.api.Assertions;

public class Test {

    @org.junit.jupiter.api.Test
    void test() {
        RMLConnector rmlConnector = new RMLConnectorImplementation();
        rmlConnector.setInstrumentationLevel(0);
        Assertions.assertTrue(rmlConnector.getInstrumentationLevel().isNoInstrumentation());
    }
    @org.junit.jupiter.api.Test
    void test2() {
        RMLConnector rmlConnector = new RMLConnectorImplementation();
        rmlConnector.setInstrumentationLevel(2);
        rmlConnector.sendNewPub("123");
        Assertions.assertTrue(true);
    }
}
