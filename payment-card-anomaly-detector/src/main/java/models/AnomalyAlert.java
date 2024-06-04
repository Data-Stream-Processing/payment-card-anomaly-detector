package src.main.java.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
import java.time.LocalDateTime;
import java.io.Serializable;
public class AnomalyAlert implements Serializable{

    @JsonProperty("alertId")
    private String alertId;
    @JsonProperty("cardId")
    private long cardId;
    @JsonProperty("fraudType")
    private String fraudType;
    @JsonProperty("fraudDetails")
    private String fraudDetails;
    @JsonProperty("timestamp")
    private String timestamp;
    
    public AnomalyAlert(){
        UUID uuid  = UUID.randomUUID();
        this.alertId = uuid.toString();
    }

    public AnomalyAlert(String alertId, long cardId, String fraudType, String fraudDetails, String timestamp){
        this.alertId = alertId;
        this.cardId = cardId;
        this.fraudType = fraudType;
        this.fraudDetails = fraudDetails;
        this.timestamp = timestamp;
    }

    public String getAlertId(){
        return alertId;
    }

    public void setAlerId(String alertId){
        this.alertId = alertId;
    }

    public long getcardId(){
        return cardId;
    }

    public void setCardId(long cardId){
        this.cardId = cardId;
    }

    public String getFraudType(){
        return fraudType;
    }

    public void setFraudType(String fraudType){
        this.fraudType = fraudType;
    }

    public String getFraudDetails(){
        return fraudDetails;
    }

    public void setFraudDetails(String fraudDetails){
        this.fraudDetails = fraudDetails;
    }

    public String getTimestanmp(){
        return timestamp;
    }

    public void setTimestmap(String timestamp){
        this.timestamp = timestamp;
    }

    public String toString(){
        return "{AnomalyAlert="
                + "alertId="
                + alertId
                + ",cardId="
                + cardId
                + ",fraudType="
                + fraudType
                + ",fraudDetails="
                + fraudDetails
                + ",timestamp="
                + timestamp
                +"}";
    }
}
