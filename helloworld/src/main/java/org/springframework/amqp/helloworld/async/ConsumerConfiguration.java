package org.springframework.amqp.helloworld.async;

import org.springframework.amqp.helloworld.HelloWorldConfiguration;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ConsumerConfiguration extends HelloWorldConfiguration {

	@Bean
	public SimpleMessageListenerContainer listenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(this.helloWorldQueueName);
		container.setMessageListener(new MessageListenerAdapter(new HelloWorldHandler()));
		return container;
	}

	@Bean
	public ConsumerConfiguration.ScheduledProducer scheduledProducer() {
		return new ConsumerConfiguration.ScheduledProducer();
	}

	@Bean
	public BeanPostProcessor postProcessor() {
		return new ScheduledAnnotationBeanPostProcessor();
	}


	static class ScheduledProducer {

		private final AtomicInteger counter = new AtomicInteger();

		@Autowired
		private SimpleMessageListenerContainer simpleMessageListenerContainer;

		private int count;

		@Scheduled(fixedRate = 3000)
		public void stop() {
			count++;
			System.out.println("Check count:"+count);
			if(count>10 && count<20){
				System.out.println("Stop Consumer");
				simpleMessageListenerContainer.stop();
			}else if(count>20){
				System.out.println("Start Consumer");
				simpleMessageListenerContainer.start();
			}
		}
	}

}
