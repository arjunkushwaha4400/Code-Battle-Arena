package com.arena.user.config;

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

    // Battle Exchange
    @Bean
    public TopicExchange battleExchange() {
        return new TopicExchange(AppConstants.BATTLE_EXCHANGE);
    }

    // Battle Result Queue
    @Bean
    public Queue battleResultQueue() {
        return QueueBuilder.durable(AppConstants.BATTLE_RESULT_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.battle.result")
                .build();
    }

    @Bean
    public Binding battleResultBinding() {
        return BindingBuilder
                .bind(battleResultQueue())
                .to(battleExchange())
                .with(AppConstants.BATTLE_COMPLETED_KEY);
    }

    // User Exchange
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(AppConstants.USER_EXCHANGE);
    }
}