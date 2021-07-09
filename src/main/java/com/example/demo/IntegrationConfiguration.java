package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class IntegrationConfiguration {

    public static final String CHANNEL_INIT = "channel.init";
    public static final String CHANNEL_1 = "channel.1";
    public static final String CHANNEL_2 = "channel.2";
    public static final String CHANNEL_3 = "channel.3";
    public static final String CHANNEL_END = "channel.end";

    @Autowired
    @Qualifier("Channel1Activator")
    private Channel1Activator channel1Activator;

    @Autowired
    @Qualifier("Channel2Activator")
    private Channel2Activator channel2Activator;

    /**
     * Channel definition
     */
    @Bean(name = CHANNEL_INIT)
    public MessageChannel channelInit() {
        return new DirectChannel();
    }

    @Bean(name = CHANNEL_1)
    public MessageChannel channel1() {
        return new DirectChannel();
    }

    @Bean(name = CHANNEL_2)
    public MessageChannel channel2() {
        return MessageChannels.direct(CHANNEL_2).get();
    }

    @Bean(name = CHANNEL_3)
    public MessageChannel channel3() {
        return new DirectChannel();
    }

    @Bean(name = CHANNEL_END)
    public MessageChannel channelEnd() {
        return new DirectChannel();
    }

    /**
     * Router definition
     */
    @Router(inputChannel = CHANNEL_INIT, resolutionRequired = "false", defaultOutputChannel = CHANNEL_3)
    public String fromChannelInitToChannel1(Message<String> message) {
        String outputChannel = "";
        if ("Tien Le".equals(message.getPayload())) {
            outputChannel = CHANNEL_1;
        } else if ("Ngoc Nguyen".equals(message.getPayload())) {
            outputChannel = CHANNEL_2;
        }
        return outputChannel;
    }

    /**
     * Integration Flow definition
     */
    @Bean
    public IntegrationFlow channel1Chain() {
        return IntegrationFlows.from(CHANNEL_1)
                .handle(new ServiceActivatingHandler(channel1Activator))
                .channel(CHANNEL_END)
                .get();
    }

    @Bean
    public IntegrationFlow channel2Chain() {
        return IntegrationFlows.from(channel2())
                .handle(new ServiceActivatingHandler(channel2Activator))
                .channel(channelEnd())
                .get();
    }

    @Bean
    public IntegrationFlow channel3Handler() {
        return IntegrationFlows.from(channel3())
                .handle(message -> System.out.println("CHANNEL_3: End, message = " + message.getPayload()))
                .get();
    }

    /*@Bean
    public IntegrationFlow channelEndHandler() {
        return IntegrationFlows.from(channelEnd())
                .handle(message -> System.out.println("CHANNEL_END: End, message = " + message.getPayload()))
                .get();
    }*/

    @Component("Channel1Activator")
    private static class Channel1Activator {
        public Message<String> process(Message<String> message) {
            return new GenericMessage<>(message.getPayload().toUpperCase());
        }
    }

    @Component("Channel2Activator")
    private static class Channel2Activator {
        public Message<String> process(Message<String> message) {
            return new GenericMessage<>(message.getPayload() + " - 22222");
        }
    }

    @ServiceActivator(inputChannel = CHANNEL_END)
    public void channelEndActivator(Message<String> message) {
        System.out.println("CHANNEL_END: End, message = " + message.getPayload());
    }
}
