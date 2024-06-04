package src.main.java.utils;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.api.common.serialization.SerializationSchema;
import com.fasterxml.jackson.core.util.*;
import src.main.java.models.AnomalyAlert;
import src.main.java.datastream.AlertSink;

public class AnomalySerializationSchema implements SerializationSchema<AnomalyAlert> {

    private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Logger LOG = LoggerFactory.getLogger(AlertSink.class);

    @Override
    public byte[] serialize(AnomalyAlert anomalyAlert){
        if(objectMapper == null){
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        }
        try{
            LOG.info(anomalyAlert.toString());
            String json = objectMapper.writeValueAsString(anomalyAlert);
            return json.getBytes();
        }catch(JsonProcessingException e){
            LOG.error("Failed to parse JSON", e);
        }
        catch (Exception e) {
            LOG.error("DUPA", e);
        }
        return new byte[0];
    }
    
}

