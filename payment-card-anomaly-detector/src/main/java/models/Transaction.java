package src.main.java.models;
import java.io.Serializable;

public class Transaction implements Serializable {
    
    private long card_id;
    private long user_id;
    private float latitude;
    private float longitude;
    private long transaction_value;
    private long spend_limit;

    public Transaction(){}

    public Transaction(long card_id, long user_id, float latitude, float longitude, long transaction_value, long spend_limit){
        this.card_id = card_id;
        this.user_id = user_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.transaction_value = transaction_value;
        this.spend_limit = spend_limit;
    }

    public long getCard_id(){
        return card_id;
    }

    public void setCard_id(long card_id){
        this.card_id = card_id;
    }

    public long getUser_id(){
        return user_id;
    }

    public void setUser_id(long user_id){
        this.user_id = user_id;
    }

    public float getLatitude(){
        return latitude;
    }

    public void setLatitude(float latitude){
        this.latitude = latitude;
    }

    public float getLongitude(){
        return longitude;
    }

    public void setLongitude(float longitude){
        this.longitude = longitude;
    }

    public float getTransaction_value(){
        return transaction_value;
    }

    public void setTransaction_value(long transaction_value){
        this.transaction_value = transaction_value;
    }

    public float getSpend_limit(){
        return spend_limit;
    }

    public void setSpend_limit(long spend_limit){
        this.spend_limit = spend_limit;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        } else if (o == null || getClass() != o.getClass()){
            return false;
        }
        Transaction that = (Transaction) o;
        return card_id == that.card_id
                && user_id == that.user_id
                && Float.compare(that.latitude,latitude) == 0
                && Float.compare(that.longitude,longitude) == 0
                && transaction_value == that.transaction_value
                && spend_limit == that.spend_limit;
     }

     @Override
     public String toString(){
        return "Transaction{"
                + "card_id="
                + card_id
                + ",user_id="
                + user_id
                + ",latitude="
                + latitude
                + ",longitute="
                + longitude
                + ",transaction_value="
                + transaction_value
                + ",spend_limit="
                + spend_limit
                + '}';
     }

}
