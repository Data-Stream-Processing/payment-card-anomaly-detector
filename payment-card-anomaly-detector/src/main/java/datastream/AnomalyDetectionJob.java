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
import java.lang.Math.*;


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

        
        FlinkKafkaProducer<AnomalyAlert> flinkKafkaProducer = new FlinkKafkaProducer<AnomalyAlert>(
                brokers,
                topicAlert,
                new AnomalySerializationSchema()
            );
        //Detect anomaly
        DataStream<AnomalyAlert> alertStream = transactionStream
                //.keyBy(transaction -> transaction.getCard_id())
                .windowAll(SlidingEventTimeWindows.of(Time.minutes(10), Time.seconds(30)))
                .aggregate(new MovingAverageAggregate())
                .filter(new TransactionValueFilter())
                .map(new MapFunction<Tuple3<MyAverage, Double, Double>, AnomalyAlert>(){
                    //@Override
                    public AnomalyAlert map(Tuple3<MyAverage, Double, Double> input) throws Exception{
                        LOG.info("ALERT!!!! " + input.f0.transaction.getCard_id());
                        return new AnomalyAlert(input.f0.transaction.getCard_id(), "HIGH_TRANSACTION_VALUE_FRAUD", 
                            "Detected high transaction for userId: "+input.f0.transaction.getCard_id()+" with tranaction value: " + input.f0.transaction.getTransaction_value()+". Average transation value is: " + input.f1);
                    }
                });
        alertStream.addSink(flinkKafkaProducer);

        
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

    public static class MovingAverageAggregate implements AggregateFunction<Transaction, MyAverage, Tuple3<MyAverage, Double, Double>> {

        private static final Logger LOG = LoggerFactory.getLogger(MovingAverageAggregate.class);
    
        @Override
        public MyAverage createAccumulator() {
            LOG.info("createAccumulator!!!!");
            return new MyAverage();
        }
    
        @Override
        public MyAverage add(Transaction value, MyAverage accumulator) {
            //LOG.info("LOOOOG: "+ Double.valueOf(accumulator.f0) + Double.valueOf(value.getTransaction_value()));
            //LOG.info("LOOOOG_add: f0: "+ Double.valueOf(accumulator.f0)+ " | f1: "+ accumulator.f1 + " | f2: "+accumulator.f2);
            LOG.info("LOG_add_1: "+ accumulator.toString());
            accumulator.sum += value.getTransaction_value();
            accumulator.count += 1;
            accumulator.var = accumulator.var + Math.pow(accumulator.transaction.getTransaction_value() - value.getTransaction_value(),2);
            accumulator.transaction = value;
            LOG.info("LOG_add_2: "+ accumulator.toString());
            return accumulator;
        }
    
        @Override
        public Tuple3<MyAverage, Double, Double> getResult(MyAverage accumulator) {
            LOG.info("LOOOOG_getResult: "+ accumulator.sum / accumulator.count + " | " + accumulator.transaction.toString());
            return new Tuple3<>(accumulator, accumulator.sum / accumulator.count, Math.sqrt(accumulator.var / accumulator.count));
        }
    
        @Override
        public MyAverage merge(MyAverage a, MyAverage b) {
            a.sum = a.sum + b.sum;
            a.count = a.count + b.count;
            return a;
        }
    }

    public static class TransactionValueFilter implements FilterFunction<Tuple3<MyAverage, Double, Double>>{
        @Override
        public boolean filter(Tuple3<MyAverage, Double, Double> input){
            //define the normal value range
            Double a = input.f1 - input.f2;
            Double b = input.f1 + input.f2;
            LOG.info("a="+ a+" | b="+b);
            //ESF
            Double esf = ((a <= Double.valueOf(input.f0.transaction.getTransaction_value())) && (Double.valueOf(input.f0.transaction.getTransaction_value()) <= b))? 0.2 : 0.8;
            LOG.info("esf="+esf);
            return (esf < 0.5)? false : true;
        }
    }

    public static class MyAverage{
        public Double sum = 0d;
        public Integer count = 0;
        public Double var = 0d;
        public Transaction transaction = new Transaction(0, 0, Double.valueOf(0), Double.valueOf(0), 0, 0);

        @Override
        public String toString(){
            return "MyAverage{"+
                    "sum=" + sum +
                    ",count=" + count +
                    //",transaction=" + transaction.getCard_id()+
                    "}";
        }
    }
    
}
