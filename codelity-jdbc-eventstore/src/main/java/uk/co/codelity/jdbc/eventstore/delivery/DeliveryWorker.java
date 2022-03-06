package uk.co.codelity.jdbc.eventstore.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DeliveryWorker {
    Logger logger = LoggerFactory.getLogger(DeliveryWorker.class);

    private final UUID consumerId;
    private final EventHandlerExecutorService eventHandlerExecutorService;
    private final DeliveryWorkFactory deliveryWorkFactory;
    private final EventDeliveryRepository eventDeliveryRepository;
    private final JdbcEventDeliveryConfig config;

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture;

    public DeliveryWorker(EventHandlerExecutorService eventHandlerExecutorService, DeliveryWorkFactory deliveryWorkFactory, EventDeliveryRepository eventDeliveryRepository, JdbcEventDeliveryConfig config) {
        this.eventHandlerExecutorService = eventHandlerExecutorService;
        this.deliveryWorkFactory = deliveryWorkFactory;
        this.eventDeliveryRepository = eventDeliveryRepository;
        this.config = config;
        this.consumerId = UUID.randomUUID();
    }

    public void start() {
        this.executorService = Executors.newFixedThreadPool(config.workerCount);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(this::run, 0, config.pollIntervalInMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        this.executorService.shutdown();
        this.scheduledExecutorService.shutdown();
        this.scheduledFuture.cancel(false);
    }

    private void run() {
        List<EventDelivery> deliveryList = null;
        try {
            deliveryList = eventDeliveryRepository.getEventsToBeDelivered(config.pollSize, consumerId, config.maxRetryCount, config.retryIntervalInSec);
        } catch (SQLException e) {
            logger.error("Events could not be read.", e);
            return;
        }

        logger.info("Delivery List count: {}", deliveryList.size());

        for (EventDelivery eventDelivery : deliveryList) {
            DeliveryWork deliveryWork = deliveryWorkFactory.create(eventDelivery, eventHandlerExecutorService, eventDeliveryRepository);
            executorService.submit(deliveryWork);
        }
    }
}
