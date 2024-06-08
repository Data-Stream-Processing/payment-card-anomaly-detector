package src.main.java.operator;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

import src.main.java.models.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MovingAverageAggregate implements AggregateFunction<Transaction, Tuple3<Double, Integer, Transaction>, Tuple2<Double, Transaction>> {

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
