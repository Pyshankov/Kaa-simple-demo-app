package org.kaaproject.kaa.example.Utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.kaaproject.kaa.example.mobile.notification.Data;

import java.io.*;
import java.nio.ByteBuffer;

public class ParsingUtils {
    public static Data parseData(String json) throws IOException {

        byte[] avroDataArray = fromJasonToAvro(json, Data.getClassSchema());
        DatumReader<Data> dataReader = new GenericDatumReader<>(Data.getClassSchema());
        Decoder dataDecoder = DecoderFactory.get().binaryDecoder(avroDataArray, null);
        GenericRecord result = dataReader.read(null,dataDecoder);
        return new Data((long)result.get("timestamp"),(ByteBuffer)result.get("data") ,(String)result.get("endpointKeyHash"),(String) result.get("hashFunction"));
    }

    public static byte[] fromJasonToAvro(String json, Schema schema) throws IOException {

        InputStream input = new ByteArrayInputStream(json.getBytes());
        DataInputStream din = new DataInputStream(input);

        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);

        DatumReader<Object> reader = new GenericDatumReader<Object>(schema);
        Object datum = reader.read(null, decoder);

        GenericDatumWriter<Object> w = new GenericDatumWriter<Object>(schema);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Encoder e = EncoderFactory.get().binaryEncoder(outputStream, null);

        w.write(datum, e);
        e.flush();

        return outputStream.toByteArray();
    }
}
