package connector.core;

public class RMLResponse {
    private boolean error;
    private RMLResponseData data;

    public boolean hasError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public RMLResponseData getData() {
        return data;
    }

    public void setData(RMLResponseData data) {
        this.data = data;
    }

    public static class RMLResponseData {
        private String agent;
        private String id;
        private String msgId;
        private String operation;
        private String topic;

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}
