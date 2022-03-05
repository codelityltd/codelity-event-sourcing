package uk.co.codelity.jdbc.eventstore.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.jdbc.eventstore.JdbcEventStore;
import uk.co.codelity.jdbc.eventstore.delivery.DeliveryWorkFactory;
import uk.co.codelity.jdbc.eventstore.delivery.DeliveryWorker;
import uk.co.codelity.jdbc.eventstore.delivery.JdbcEventDeliveryConfig;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;
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

    @Value("${eventstore.watcher.workerCount:1}")
    private int workerCount;

    @Value("${eventstore.watcher.pollSize:1}")
    private int pollSize;

    @Value("${eventstore.watcher.pollInterval:200}")
    private int pollInterval;

    @Value("${eventstore.watcher.maxRetryCount:5}")
    private int maxRetryCount;

    @Value("${eventstore.watcher.retryIntervalInSec:30}")
    private int retryIntervalInSec;

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

    @Bean
    public EventDeliveryRepository eventDeliveryRepository(){
        return new EventDeliveryRepository(url, user, password);
    }

    @ConditionalOnMissingBean
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    @Bean
    public DeliveryWorker deliveryWorker(EventHandlerExecutorService eventHandlerExecutorService){
        JdbcEventDeliveryConfig config = new JdbcEventDeliveryConfig(workerCount, pollSize, maxRetryCount, retryIntervalInSec, pollInterval);
        return new DeliveryWorker(eventHandlerExecutorService, new DeliveryWorkFactory(), eventDeliveryRepository(), config);
    }

    @Bean
    public ApplicationEventListener applicationEventListenerForJdbcEventStore(DeliveryWorker deliveryWorker){
        return new ApplicationEventListener(deliveryWorker);
    }

    @PostConstruct
    public void printConfigurationMessage() {
       logger.info("Auto-configuration for JDBCEventStore is completed.");
    }
}
