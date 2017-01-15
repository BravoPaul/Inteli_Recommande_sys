package com.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * 详细可以参考：https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example
 * @author Fung
 *
 */
public class ProducerDemo {
	public static void main(String[] args) {
		Random rnd = new Random();
		int events=100;

		// 设置配置属性
		Properties props = new Properties();
		props.put("metadata.broker.list","127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		// key.serializer.class默认为serializer.class
		props.put("key.serializer.class", "kafka.serializer.StringEncoder");
		// 可选配置，如果不配置，则使用默认的partitioner
		props.put("partitioner.class", "com.producer.PartitionerDemo");
		// 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
		// 值为0,1,-1,可以参考
		// http://kafka.apache.org/08/configuration.html
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);

		// 创建producer
		Producer<String, String> producer = new Producer<String, String>(config);
		// 这里为了简化程序，我们假定只有5种商品
		long start=System.currentTimeMillis();
		ArrayList<String> Pthings = new ArrayList<String>();
		Pthings.add("a");
		Pthings.add("b");
		Pthings.add("c");
		Pthings.add("d");
		Pthings.add("e");
		
		int count = 0;
		int i= 0;
		//这个死循环用来模拟用户的行为，因为互联网中用户的行为是海量的。
		while(true){
			//为了简化模型，我们假定用户每一万次循环就会因为各种因素选择不同的商品，所以我们利用随机数更新一个用户选择的商品
			if (count>10000) {
				i = (int)(Math.random() * 4);
				count = 0;
				System.out.println("用户行为发生变化"+Pthings.get(i));
			}
			
			String thing1 = Pthings.get(i);//rnd.nextInt(255);
			
			String msg = thing1;
			//如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(
					"Movie", "Thing", msg);
			//producer向kafka发送数据
			producer.send(data);
			count++;
		}
	}
}