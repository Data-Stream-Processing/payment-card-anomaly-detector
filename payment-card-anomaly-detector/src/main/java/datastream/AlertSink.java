package src.main.java.datastream;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import src.main.java.models.AnomalyAlert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertSink implements SinkFunction<AnomalyAlert>{
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(AlertSink.class);

    @Override
    public void invoke(AnomalyAlert value, Context context){
        LOG.info(value.toString());
    }
}
