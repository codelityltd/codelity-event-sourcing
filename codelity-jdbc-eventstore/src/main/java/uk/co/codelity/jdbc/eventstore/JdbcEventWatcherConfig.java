package uk.co.codelity.jdbc.eventstore;

public class JdbcEventWatcherConfig {
    public final int workerCount;
    public final int pollSize;
    public final int maxRetryCount;
    public final int retryIntervalInSec;

    public JdbcEventWatcherConfig(final int workerCount, final int pollSize, final int maxRetryCount, final int retryIntervalInSec) {
        this.workerCount = workerCount;
        this.pollSize = pollSize;
        this.maxRetryCount = maxRetryCount;
        this.retryIntervalInSec = retryIntervalInSec;
    }
}
