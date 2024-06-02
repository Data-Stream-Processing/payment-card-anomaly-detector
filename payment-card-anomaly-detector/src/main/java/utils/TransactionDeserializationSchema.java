package src.main.java.utils;

import src.main.java.models.Transaction;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;

public class TransactionDeserializationSchema implements DeserializationSchema<Transaction> {

    private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Transaction deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Transaction.class);
    }

    @Override
    public boolean isEndOfStream(Transaction transaction) {
        return false;
    }

    @Override
    public TypeInformation<Transaction> getProducedType() {
        return TypeInformation.of(Transaction.class);
    }
}

