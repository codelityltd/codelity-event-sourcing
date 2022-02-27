package uk.co.codelity.jdbc.eventstore.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.co.codelity.jdbc.eventstore.delivery.DeliveryWorker;

@Component
public class ApplicationEventListener {
    Logger logger = LoggerFactory.getLogger(ApplicationEventListener.class);

    private DeliveryWorker deliveryWorker;

    @Autowired
    public ApplicationEventListener(DeliveryWorker deliveryWorker) {
        this.deliveryWorker = deliveryWorker;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Delivery Worker is starting...");
        deliveryWorker.start();
    }

    @EventListener
    public void onApplicationEvent(ContextStoppedEvent event) {
        logger.info("Delivery Worker is shutting down...");
        deliveryWorker.stop();
    }
}
