package src.main.java.datastream;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.walkthrough.common.sink.AlertSink;
import org.apache.flink.walkthrough.common.entity.Alert;
import org.apache.flink.walkthrough.common.source.TransactionSource;

import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.kafka.clients.producer.ProducerConfig;

import src.main.java.models.Transaction;
import src.main.java.utils.TransactionDeserializationSchema;

import java.util.Properties;

/**
 * Skeleton code for the datastream walkthrough
 */
public class AnomalyDetectionJob {

	static String brokers = "localhost:9092";
    static String groupId = "test-group";
    static String topic = "test-topic";

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", brokers);
        properties.setProperty("group.id", groupId);

        FlinkKafkaConsumer<Transaction> consumer = new FlinkKafkaConsumer<>(
				topic,
                new TransactionDeserializationSchema(),
                properties);

        DataStream<Transaction> transactionStream = env.addSource(consumer);

		transactionStream.print();

		env.execute("Fraud Detection");
	}
}
