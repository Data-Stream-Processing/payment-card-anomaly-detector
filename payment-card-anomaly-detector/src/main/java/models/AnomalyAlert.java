package src.main.java.models;

public class AnomalyAlert {

    private String alertId;
    private long cardId;
    private String fraudType;
    private String fraudDetails;
    
    public AnomalyAlert(){}

    public AnomalyAlert(String alertId, long cardId, String fraudType, String fraudDetails){
        this.alertId = alertId;
        this.cardId = cardId;
        this.fraudType = fraudType;
        this.fraudDetails = fraudDetails;
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
                +"}";
    }
}
