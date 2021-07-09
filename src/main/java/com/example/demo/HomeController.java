package com.example.demo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.IntegrationConfiguration.CHANNEL_INIT;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final MessageChannel channelInit;

    public HomeController(@Qualifier(CHANNEL_INIT) MessageChannel channelInit) {
        this.channelInit = channelInit;
    }

    @GetMapping()
    public void putMessageToChannel(@RequestParam("message") String message) {
        channelInit.send(new GenericMessage<>(message));
    }
}
