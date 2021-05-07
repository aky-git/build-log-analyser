package org.akycorp.build.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * @author  : ajay_kumar_yadav
 * @created : 05/05/21
 */
public class BuildEvent {
    private String eventId;
    private String type;
    private String host;
    private Long startTime;
    private Long finishTime;

    public volatile AtomicBoolean isProcessed = new AtomicBoolean(false);


    public boolean isProcessed() {
        return this.isProcessed.compareAndSet(true, false);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public Long getDuration() {
        if(Objects.nonNull(this.finishTime) && Objects.nonNull(this.startTime))
            return this.finishTime - this.startTime;
        return null;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        if(Objects.nonNull(this.finishTime)){
            this.isProcessed.set(true);
        }
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
        if(Objects.nonNull(this.startTime)){
            this.isProcessed.set(true);
        }
    }

    public String getAlertFlag() {
        if(Objects.nonNull(getDuration()) && getDuration() > 4)
            return "true";
        return "false";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildEvent that = (BuildEvent) o;
        return eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "BuildEvent{" +
                "eventId='" + eventId + '\'' +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                '}';
    }
}
