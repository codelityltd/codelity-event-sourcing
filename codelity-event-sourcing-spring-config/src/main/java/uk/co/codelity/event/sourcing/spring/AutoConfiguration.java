package uk.co.codelity.event.sourcing.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.core.bootstrap.Bootstrapper;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.scanner.AggregateEventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventScanner;
import uk.co.codelity.event.sourcing.core.service.AggregateService;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

import javax.annotation.PostConstruct;
import java.util.Map;


@Configuration
public class AutoConfiguration {
    Logger logger = LoggerFactory.getLogger(AutoConfiguration.class);

    @Bean
    public Bootstrapper bootstrapper() {
        return new Bootstrapper(new EventScanner(), new AggregateEventHandlerScanner(), new EventHandlerScanner());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public EventSourcingContext eventHandlingContext(ApplicationContext applicationContext, Bootstrapper bootstrapper) throws Exception {
        return bootstrapper.initContext(findApplicationPackageName(applicationContext));
    }

    @Bean
    public AggregateService aggregateService(EventStore eventStore,
                                             EventSourcingContext eventSourcingContext,
                                             ObjectFactory objectFactory,
                                             ObjectMapper objectMapper){
        return new AggregateService(eventStore, eventSourcingContext, objectFactory, objectMapper);
    }

    @Bean
    public ObjectFactory objectFactory(ApplicationContext applicationContext) {
        return new SpringObjectFactory(applicationContext);
    }

    @PostConstruct
    public void printConfigurationMessage() {
        logger.info("Auto-configuration for 'codelity-event-sourcing-core' is complete...");
    }


    private String findApplicationPackageName(ApplicationContext applicationContext){
      
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        if(beans.isEmpty()) {
            logger.warn("SpringBootApplication could not be found!");
            return null;
        }

        Object bean = beans.values().iterator().next();
        return bean.getClass().getPackageName();
    }
}
