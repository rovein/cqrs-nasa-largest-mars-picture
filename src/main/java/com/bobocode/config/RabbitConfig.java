package com.bobocode.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Exchange largestPictureCommandExchange() {

        return new DirectExchange("largest-picture-command-exchange", true, false);
    }

    @Bean
    public Queue largestPictureCommandQueue() {
        return QueueBuilder.durable("largest-picture-command-queue")
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(largestPictureCommandQueue())
                .to(largestPictureCommandExchange())
                .with(StringUtils.EMPTY)
                .noargs();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
