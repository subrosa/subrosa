package com.subrosagames.subrosa.bootstrap;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for messaging.
 */
@Configuration
public class AmqpConfiguration {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocVariable

    /**
     * Holder for queue and exchange names.
     */
    public static final class QueueName {

        public static final String USER_ADDRESS = "user-address";
        public static final String USER_ADDRESS_EXCHANGE = "user-address-exchange";
    }

    @Bean
    public TopicExchange userAddressExchange() {
        return new TopicExchange(QueueName.USER_ADDRESS_EXCHANGE);
    }

    @Bean
    public Queue userAddressQueue() {
        return new Queue(QueueName.USER_ADDRESS);
    }

    @Bean
    public Binding userAddressBinding(Queue userAddressQueue, TopicExchange userAddressExchange) {
        return BindingBuilder.bind(userAddressQueue).to(userAddressExchange).with(QueueName.USER_ADDRESS);
    }

    // CHECKSTYLE-ON: JavadocVariable
    // CHECKSTYLE-ON: JavadocMethod
}
