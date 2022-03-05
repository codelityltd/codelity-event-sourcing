package uk.co.codelity.jdbc.eventstore.delivery;

public class JdbcEventDeliveryConfig {
    public final int workerCount;
    public final int pollSize;
    public final int maxRetryCount;
    public final int retryIntervalInSec;
    public final long pollIntervalInMs;

    public JdbcEventDeliveryConfig(final int workerCount,
                                   final int pollSize,
                                   final int maxRetryCount,
                                   final int retryIntervalInSec,
                                   final long pollIntervalInMs) {
        this.workerCount = workerCount;
        this.pollSize = pollSize;
        this.maxRetryCount = maxRetryCount;
        this.retryIntervalInSec = retryIntervalInSec;
        this.pollIntervalInMs = pollIntervalInMs;
    }
}
