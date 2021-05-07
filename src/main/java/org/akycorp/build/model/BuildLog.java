package org.akycorp.build.model;

/*
 * @author  : ajay_kumar_yadav
 * @created : 05/05/21
 */
public class BuildLog {
    private String id;
    private String type;
    private String host;
    private long timestamp;
    private String state;

    public static void populateBuildEvent(BuildLog log, BuildEvent buildEvent) {
        buildEvent.setEventId(log.getId());
        buildEvent.setHost(log.getHost());
        buildEvent.setType(log.getType());
        if("STARTED".equalsIgnoreCase(log.getState())){
            buildEvent.setStartTime(log.getTimestamp());
        }
        if("FINISHED".equalsIgnoreCase(log.getState())){
            buildEvent.setFinishTime(log.getTimestamp());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "BuildLog{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", timestamp=" + timestamp +
                ", state='" + state + '\'' +
                '}';
    }
}
