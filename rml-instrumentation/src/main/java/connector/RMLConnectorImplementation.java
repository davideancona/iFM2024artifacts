package connector;

import connector.core.*;
import com.google.gson.Gson;
import jakarta.websocket.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@ClientEndpoint()
public class RMLConnectorImplementation implements RMLConnector {

    private static Session session;

    private final Gson gson = new Gson();

    private InstrumentationLevels instrumentationLevel;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private boolean responseReceived = false;


    public void connect() {
        if (session == null) {
            try {
                final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                session = container.connectToServer(this, URI.create("ws://localhost:80/"));
            } catch (Exception e) {
                System.err.println(getDateAndThreadName() + "Connect : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * <code>pub(pubId, pubTopic, msgId) matches {agent: 'pub', operation: 'send', id: pubId, topic: pubTopic, msgId: msgId};</code>
     */
    @Override
    public void sendMessage(String agentId, String topic, String msgId) {
        runIfInstrumented(it -> {
            RMLMessage rmlMessage = new RMLMessage();
            rmlMessage.setMsgId(msgId);
            rmlMessage.setAgent(RMLAgents.PUBLISHER.toString());
            rmlMessage.setId(agentId);
            rmlMessage.setTopic(topic);
            rmlMessage.setOperation(RMLOperations.SEND.toString());

            effectiveSend(rmlMessage);
        });
    }

    final ReentrantLock lock = new ReentrantLock();
    private void effectiveSend(RMLMessage RMLMessage) {
        if (instrumentationLevel.isFull()) {
            try {
                lock.lock();
                try {
                    session.getBasicRemote().sendText(RMLMessage.toJson());
                } finally {
                    lock.unlock();
                }

                // wait for response
                synchronized (this) {
                    while (!responseReceived) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            System.err.println(getDateAndThreadName() + "effectiveSend : " + e.getMessage());
                        }
                    }
                    responseReceived = false;
                }
            } catch (Exception e) {
                System.err.println(getDateAndThreadName() + "effectiveSend : " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }



    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void processMessageFromServer(String message, Session session) {
        RMLResponse response = gson.fromJson(message, RMLResponse.class);
        if (response.hasError()) {
            System.err.println(getDateAndThreadName() + "ERROR RECEIVED FROM MONITOR");
            System.exit(-1);
        }
        synchronized (this) {
            responseReceived = true;
            this.notifyAll();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println(getDateAndThreadName() + "onError : " + throwable.getMessage());
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        session.close();
        this.session.close();
    }

    /**
     * <code>subscription(subId, subTopic) matches {agent: 'sub', operation: 'subscription', id: subId, topic: subTopic};</code>
     */
    @Override
    public void sendSubscription(String agentId, String topic) {
        runIfInstrumented(it -> {
            RMLMessage rmlMessage = new RMLMessage();
            rmlMessage.setAgent(RMLAgents.SUBSCRIBER.toString());
            rmlMessage.setId(agentId);
            rmlMessage.setTopic(topic.substring(0, topic.length() - 2));
            rmlMessage.setOperation(RMLOperations.SUBSCRIPTION.toString());

            effectiveSend(rmlMessage);
        });
    }

    /**
     * <code>recv(subId, subTopic, msgId, pubId) matches {agent: 'sub', operation: 'receive', id: subId, topic: subTopic, msgId: msgId, sender: pubId};</code>
     */
    @Override
    public void sendReceipt(String agentId, String topic, String msgId, String sender) {
        runIfInstrumented(it -> {
            RMLMessage rmlMessage = new RMLMessage();
            rmlMessage.setMsgId(msgId);
            rmlMessage.setAgent(RMLAgents.SUBSCRIBER.toString());
            rmlMessage.setId(agentId);
            rmlMessage.setTopic(topic.substring(0, topic.length() - 2));
            rmlMessage.setOperation(RMLOperations.RECEIVE.toString());
            rmlMessage.setSender(sender);

            effectiveSend(rmlMessage);
        });
    }

    /**
     * <code>new_pub(pubId) matches {agent: 'pub', operation:'new', id:pubId};</code>
     */
    @Override
    public void sendNewPub(String pubId) {
        runIfInstrumented(it -> {
            RMLMessage rmlMessage = new RMLMessage();
            rmlMessage.setAgent(RMLAgents.PUBLISHER.toString());
            rmlMessage.setOperation(RMLOperations.NEW.toString());
            rmlMessage.setId(pubId);

            effectiveSend(rmlMessage);
        });
    }

    @Override
    public InstrumentationLevels getInstrumentationLevel() {
        return instrumentationLevel;
    }

    @Override
    public void setInstrumentationLevel(int instrumentationLevel) {
        this.instrumentationLevel = InstrumentationLevels.values()[instrumentationLevel];
    }

    /**
     * It runs <code>callback</code> if the instrumentation level is partial or full
     */
    private void runIfInstrumented(Consumer<Void> callback) {
        if (instrumentationLevel.isPartial() || instrumentationLevel.isFull()) {
            callback.accept(null);
        }
    }


    private String getDateAndThreadName() {
        return sdf.format(new Date()) + " [" + Thread.currentThread().getName() + "] ";
    }
}
