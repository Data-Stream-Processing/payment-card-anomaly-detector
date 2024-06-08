package src.main.java.datastream;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.walkthrough.common.entity.Alert;
import org.apache.flink.walkthrough.common.source.TransactionSource;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;


import src.main.java.models.*;
import src.main.java.utils.*;
//import src.main.java.operator.*;
import java.io.*;


import java.util.Properties;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeleton code for the datastream walkthrough
 */
public class AnomalyDetectionJob {

    private static final Logger LOG = LoggerFactory.getLogger(AnomalyDetectionJob.class);

	static String brokers = "localhost:9092";
    static String groupId = "test-group";
    static String topic = "test-topic";
    static String topicAlert = "test-alert";

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", brokers);
        properties.setProperty("group.id", groupId);

        FlinkKafkaConsumer<Transaction> consumer = new FlinkKafkaConsumer<>(
			topic,
            new TransactionDeserializationSchema(),
            properties);

        DataStream<Transaction> transactionStream = env.addSource(consumer)
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<Transaction>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                    .withTimestampAssigner((event, timestamp) -> System.currentTimeMillis())
            ).name("transaction-kafka-consumer");

        //Detect anomaly
        DataStream<AnomalyAlert> alertStream = transactionStream
                .keyBy(transaction -> transaction.getCard_id())
                .window(SlidingProcessingTimeWindows.of(Time.minutes(10), Time.minutes(3)))
                //.aggregate(new MovingAverageAggregate(), new AnomalyDetectionWindowFunction());
                .aggregate(new MovingAverageAggregate())
                .filter(data -> data.f0 > data.f1.getTransaction_value())
                .map(new MapFunction<Tuple2<Double, Transaction>, AnomalyAlert>(){
                    //@Override
                    public AnomalyAlert map(Tuple2<Double, Transaction> input) throws Exception{
                        LOG.info("ALERT!!!! " + input.f1.getCard_id());
                        return new AnomalyAlert(input.f1.getCard_id(), "FRAUD_DETECT_ANOMALY", "test");
                    }
                });
        
        
       /*  alertStream
                .addSink(new FlinkKafkaProducer<>(
                        brokers,
                        topicAlert,
                        new AnomalySerializationSchema()   
                )); */

      /*          
        DataStream<AnomalyAlert> anomalyAlert = transactionStream
            .keyBy(Transaction::getCard_id)
            .process(new AnomalyDetector())
            .name("anomaly-detector");

		transactionStream.print();

        anomalyAlert
            .addSink(new AlertSink())
            .name("send-alerts");
        
        FlinkKafkaProducer<AnomalyAlert> flinkKafkaProducer = new FlinkKafkaProducer<AnomalyAlert>(
            brokers,
            topicAlert,
            new AnomalySerializationSchema()
        );
        
        DataStream<AnomalyAlert> anomalyAlertKafka = transactionStream
            .keyBy(Transaction::getCard_id)
            .process(new AnomalyDetector())
            .name("anomaly-detector-kafka");
        
            anomalyAlertKafka
            //.timeWindowAll(Time.hours(24))
            .addSink(flinkKafkaProducer);
                */

		env.execute("Kafka-anomalyDetection");
	}

    public static class MovingAverageAggregate implements AggregateFunction<Transaction, Tuple3<Double, Integer, Transaction>, Tuple2<Double, Transaction>> {

        private static final Logger LOG = LoggerFactory.getLogger(MovingAverageAggregate.class);
    
        @Override
        public Tuple3<Double, Integer, Transaction> createAccumulator() {
            return new Tuple3<>(0.0, 0, new Transaction());
        }
    
        @Override
        public Tuple3<Double, Integer, Transaction> add(Transaction value, Tuple3<Double, Integer, Transaction> accumulator) {
            //LOG.info("LOOOOG: "+ Double.valueOf(accumulator.f0) + Double.valueOf(value.getTransaction_value()));
            LOG.info("LOOOOG_add: f0: "+ Double.valueOf(accumulator.f0)+ " | f1: "+ accumulator.f1 + " | f2: "+accumulator.f2);
            return new Tuple3<>(Double.valueOf(accumulator.f0) + Double.valueOf(value.getTransaction_value()), accumulator.f1 + 1, value);
        }
    
        @Override
        public Tuple2<Double, Transaction> getResult(Tuple3<Double, Integer, Transaction> accumulator) {
            LOG.info("LOOOOG 1: "+ Double.valueOf(accumulator.f0) / Double.valueOf(accumulator.f1) + " | " + accumulator.f2.toString());
            return new Tuple2<>(Double.valueOf(accumulator.f0) / Double.valueOf(accumulator.f1), accumulator.f2);
        }
    
        @Override
        public Tuple3<Double, Integer, Transaction>  merge(Tuple3<Double, Integer, Transaction> a, Tuple3<Double, Integer, Transaction> b) {
            a.f0 = a.f0 + b.f0;
            a.f1 = a.f1 + b.f1;
            return a;
        }
    }
    
}
