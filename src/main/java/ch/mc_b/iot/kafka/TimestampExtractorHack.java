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

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * Hack: weil Timestamp nicht negativ sein darf.
 * @see https://stackoverflow.com/questions/48427964/kafka-streams-custom-timestampextractor-for-aggregation?rq=1
 */
public class TimestampExtractorHack implements TimestampExtractor
{
    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long previousTimestamp)
    {
        final long timestamp = consumerRecord.timestamp();

        if ( timestamp < 0 )
            return System.currentTimeMillis();

        return timestamp;
    }
}