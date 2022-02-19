package uk.co.codelity.jdbc.eventstore.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.jdbc.eventstore.JdbcEventStore;
import uk.co.codelity.jdbc.eventstore.repository.EventRepository;

import javax.annotation.PostConstruct;


@Configuration
public class JdbcEventStoreConfiguration {
    Logger logger = LoggerFactory.getLogger(JdbcEventStoreConfiguration.class);

    @Value("${eventstore.jdbc.url}")
    private String url;

    @Value("${eventstore.jdbc.user}")
    private String user;

    @Value("${eventstore.jdbc.password}")
    private String password;

    @Bean
    public JdbcEventStore eventStore(final EventRepository eventRepository,
                                     final EventHandlerRegistry eventHandlerRegistry,
                                     final ObjectMapper objectMapper){
        return new JdbcEventStore(eventRepository, eventHandlerRegistry, objectMapper);
    }

    @Bean
    public EventRepository eventRepository(){
        return new EventRepository(url, user, password);
    }

    @ConditionalOnMissingBean
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @PostConstruct
    public void printConfigurationMessage() {
       logger.info("Auto-configuration for JDBCEventStore is completed.");
    }
}
