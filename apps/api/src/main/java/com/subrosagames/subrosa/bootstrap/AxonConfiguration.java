package com.subrosagames.subrosa.bootstrap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.axonframework.commandhandling.AsynchronousCommandBus;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.axonframework.serializer.Serializer;
import org.axonframework.serializer.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration for messaging and command handling.
 */
@Configuration
public class AxonConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    CommandBus commandBus() {
        return new AsynchronousCommandBus();
    }

    @Bean
    AnnotationCommandHandlerBeanPostProcessor annotationCommandHandlerBeanPostProcessor(CommandBus commandBus) {
        AnnotationCommandHandlerBeanPostProcessor handler = new AnnotationCommandHandlerBeanPostProcessor();
        handler.setCommandBus(commandBus);
        return handler;
    }

    @Bean
    CommandGateway commandGateway(CommandBus commandBus) {
        //return new DefaultCommandGateway(commandBus, retryScheduler, commandDispatchInterceptors);
        return new DefaultCommandGateway(commandBus);
    }

    @Bean
    EventStore eventStore(Serializer serializer) throws IOException {
        Path tempDirectory = Files.createTempFile("axon", "_events");
        SimpleEventFileResolver fileResolver = new SimpleEventFileResolver(tempDirectory.toFile());
        return new FileSystemEventStore(serializer, fileResolver);
    }

    @Bean
    Serializer serializer() {
        return new JacksonSerializer(objectMapper);
    }

}
