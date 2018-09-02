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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.StreamsConfig;

/**
 * Abhandlung Alerts 
 */
public class AlertConsumer
{
    private static final String REST_URI = "http://camunda:8080/engine-rest/process-definition/key/"; // <prozess>/start
    private static Client client = ClientBuilder.newClient();
  
    public static void main(String[] args) throws Exception
    {
        Properties props = new Properties();
        props.put( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092" );
        props.put( "group.id", "alert" );
        props.put( "enable.auto.commit", "true" );
        props.put( "auto.commit.interval.ms", "1000" );
        props.put( "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer" );
        props.put( "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer" );
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>( props );
        consumer.subscribe( Arrays.asList( "broker_message" ) );

        System.out.println( "AlertConsumer" );
        while (true)
        {
            ConsumerRecords<String, String> records = consumer.poll( 100 );
            for ( ConsumerRecord<String, String> record : records )
            {
                long offset = record.offset();
                String value = record.value();
                if ( value != null && value.startsWith( "alert" ) )
                {
                    String text =  "{ \"variables\": { \"rnr\": {\"value\": " + 123 + ", \"type\": \"long\"}, " + 
                                    "\"rbetrag\": {\"value\": " + 100.0 + ", \"type\": \"String\"} } }";
                    System.out.printf( "offset = %d, value = %s%n", offset, record.value() );
                    Response rc = client.target( REST_URI )
                                    .path( "RechnungStep3/start" )
                                    .request(MediaType.TEXT_PLAIN)
                                    .post(Entity.entity( text , MediaType.TEXT_PLAIN ) );
                    System.out.println( rc.getStatus() );
                }
            }
        }
    }

}
