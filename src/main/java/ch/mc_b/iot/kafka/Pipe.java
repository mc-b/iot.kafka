/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this
 * file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless
 * required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package ch.mc_b.iot.kafka;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL that reads from a
 * source topic "streams-plaintext-input", where the values of messages represent lines of text, and writes the
 * messages as-is into a sink topic "streams-pipe-output".
 */
public class Pipe
{
    public static void main(String[] args) throws Exception
    {
        Properties props = new Properties();
        props.put( StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe" );
        props.put( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092" );
        props.put( StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass() );
        props.put( StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass() );
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TimestampExtractorHack.class);

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> source = builder.stream( "broker_message" );
        KStream<String, String> output = source.flatMapValues( new ValueMapper<String, Iterable<String>>()
        {
            @Override
            public Iterable<String> apply(String value)
            {
                String[] values = value.split( "," );
                // Temperatur und Luftfeuchtigsensor
                if  ( "0xBC".equals( values[0].trim() ) )
                {
                    System.out.println( "{" + "\"humtemp\": { \"temp\": " + values[1] + ", \"hum\": " + values[2] + " } }" );
                    return  ( Arrays.asList( new String[] { "{", "\"humtemp\": { \"temp\": ", values[1], ", \"hum\": ", values[2], " } }"  } ) );
                }
                System.out.println( Arrays.asList( value.split( "," ) ) );
                return Arrays.asList( value.split( "," ) );
            }
        } );
        output.to( "iot" );

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams( topology, props );
        final CountDownLatch latch = new CountDownLatch( 1 );

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook( new Thread( "streams-shutdown-hook" )
        {
            @Override
            public void run()
            {
                streams.close();
                latch.countDown();
            }
        } );

        try
        {
            streams.start();
            latch.await();
        }
        catch (Throwable e)
        {
            System.exit( 1 );
        }
        System.exit( 0 );
    }
}
