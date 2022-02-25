package uk.co.codelity.jdbc.eventstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JdbcEventWatcher {
    Logger logger = LoggerFactory.getLogger(JdbcEventWatcher.class);

    private final EventDeliveryRepository eventDeliveryRepository;
    private final JdbcEventWatcherConfig config;
    private final EventHandlerExecutorService eventHandlerExecutorService;

    private ScheduleManager scheduleManager;

    public JdbcEventWatcher(final EventDeliveryRepository eventDeliveryRepository, final EventHandlerExecutorService eventHandlerExecutorService, final JdbcEventWatcherConfig config) {
        this.eventDeliveryRepository = eventDeliveryRepository;
        this.config = config;
        this.eventHandlerExecutorService = eventHandlerExecutorService;
    }

    public void start() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(config.workerCount);

        List<Worker> workers = IntStream.range(0, config.workerCount)
                .mapToObj(n -> new Worker())
                .collect(Collectors.toUnmodifiableList());

        scheduleManager = new ScheduleManager(scheduledExecutorService, workers);
        scheduleManager.start();
    }

    public void stop() {
        scheduleManager.stop();
    }

    class ScheduleManager implements Runnable {
        private ScheduledFuture<?> managerScheduledFuture;
        private ScheduledExecutorService scheduledExecutorService;
        private List<Worker> workers;
        private List<ScheduledFuture<?>> scheduledFutures;

        public ScheduleManager(ScheduledExecutorService scheduledExecutorService, List<Worker> workers) {
            this.scheduledExecutorService = scheduledExecutorService;
            this.workers = workers;
            this.scheduledFutures = new ArrayList<>();
        }

        public void start() {
            managerScheduledFuture = Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(this::run,
                            0,
                            5,
                            TimeUnit.MINUTES);
        }

        public void stop() {
            managerScheduledFuture.cancel(false);
            try {
                cancelTasks();
            } catch (ExecutionException e) {
                logger.error(e.getMessage(), e);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void run() {
            try {
                cancelTasks();

                for (Worker worker : workers) {
                    scheduledFutures.add(scheduledExecutorService.scheduleAtFixedRate(
                            worker,
                            0,
                            200,
                            TimeUnit.MILLISECONDS));
                }
            } catch (InterruptedException ex) {
               logger.error(ex.getMessage(), ex);
               Thread.currentThread().interrupt();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

        }

        private void cancelTasks() throws ExecutionException, InterruptedException {
            for (ScheduledFuture<?> scheduledFuture : scheduledFutures) {
                scheduledFuture.cancel(false);
                scheduledFuture.get();
            }

            scheduledFutures.clear();
        }
    }

    class Worker implements Runnable {
        UUID consumerId;

        public Worker() {
            this.consumerId = UUID.randomUUID();
        }

        @Override
        public void run() {
            List<EventDelivery> deliveryList = null;
            try {
                deliveryList = eventDeliveryRepository.getEventsToBeDelivered(config.pollSize, consumerId, config.maxRetryCount, config.retryIntervalInSec);
            } catch (SQLException e) {
                logger.error("Events could not be read.", e);
                return;
            }

            for (EventDelivery eventDelivery : deliveryList) {
                try {
                    EventInfo eventInfo = convertToEventInfo(eventDelivery.event);
                    eventHandlerExecutorService.execute(eventInfo, eventDelivery.handlerCode);
                    deliveryCompleted(eventDelivery);
                } catch (EventHandlerException e) {
                    logger.error(String.format("Event (DeliveryId: %s) could not be processed.", eventDelivery.id), e);
                    deliveryFailed(eventDelivery);
                }
            }
        }

        private void deliveryCompleted(EventDelivery eventDelivery) {
            try {
                eventDeliveryRepository.markDeliveryAsCompleted(eventDelivery);
            } catch (SQLException e) {
                logger.error(String.format("Event (DeliveryId: %s) has been processed but could not be set as completed.",
                        eventDelivery.id), e);
            }
        }

        private void deliveryFailed(EventDelivery eventDelivery) {
            try {
                eventDeliveryRepository.markDeliveryAsFailed(eventDelivery.id);
            } catch (SQLException e) {
                logger.error(String.format("Event delivery (DeliveryId: %s) failure could not be saved.", eventDelivery.id)
                        , e);
            }
        }

        private EventInfo convertToEventInfo(Event event) {
            return new EventInfo(event.streamId, event.position, event.name, event.metadata, event.payload);
        }
    }
}
