package com.arena.execution.config;

import com.arena.common.constants.AppConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // Code Exchange
    @Bean
    public DirectExchange codeExchange() {
        return new DirectExchange(AppConstants.CODE_EXCHANGE);
    }

    // Code Execution Queue
    @Bean
    public Queue codeExecutionQueue() {
        return QueueBuilder.durable(AppConstants.CODE_EXECUTION_QUEUE)
                .withArgument("x-message-ttl", 60000)
                .build();
    }

    @Bean
    public Binding codeExecutionBinding() {
        return BindingBuilder
                .bind(codeExecutionQueue())
                .to(codeExchange())
                .with(AppConstants.CODE_EXECUTE_KEY);
    }

    // Code Result Queue
    @Bean
    public Queue codeResultQueue() {
        return QueueBuilder.durable(AppConstants.CODE_RESULT_QUEUE)
                .build();
    }

    @Bean
    public Binding codeResultBinding() {
        return BindingBuilder
                .bind(codeResultQueue())
                .to(codeExchange())
                .with(AppConstants.CODE_RESULT_KEY);
    }
}