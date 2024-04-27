package connector;

import connector.core.InstrumentationLevels;

public interface RMLConnector {
    InstrumentationLevels getInstrumentationLevel();

    void setInstrumentationLevel(int instrumentationLevel);

    void connect();

    void sendMessage(String agentId, String topic, String msgId);

    void sendNewPub(String pubId);

    void sendReceipt(String agentId, String topic, String msgId, String sender);

    void sendSubscription(String agentId, String topic);

}
