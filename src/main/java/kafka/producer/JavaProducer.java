/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.kafka.producer;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.message.Message;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;



/**
 * run sample:
 * bin/kafka-create-topic.sh --topic page_visits --replica 3 --zookeeper localhost:2181 --partition 5
 */

public final class JavaProducer {

	public static void main(String[] args) {

		try {
			File f = new File("/home/jwang/github/search-perf/data/cars1m.json");
			Properties props = new Properties();
			props.put("zk.connect", "localhost:2181");
			props.put("serializer.class", "kafka.serializer.DefaultEncoder");
			ProducerConfig producerConfig = new ProducerConfig(props);
			Producer<String, Message> kafkaProducer = new Producer<String, Message>(
					producerConfig);
			String topic = "perfTopic";
			List<KeyedMessage<String, Message>> msgList = new ArrayList<KeyedMessage<String, Message>>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF-8"));
			int batchSize = 10000;
			int count = 0;
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				count++;
				System.out.println(count + " msgs pushed.");
				Message m = new Message(line.getBytes("UTF-8"));
				KeyedMessage<String, Message> msg = new KeyedMessage<String, Message>(topic, m);
				msgList.add(msg);
				if (msgList.size() > batchSize) {
					kafkaProducer.send(msgList);
					msgList.clear();
				}
			}
			if (msgList.size() > 0) {
				kafkaProducer.send(msgList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
