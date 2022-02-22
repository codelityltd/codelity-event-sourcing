package uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.custom;

import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.EventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;

@EventSourcingEnabled
@EventScan(basePackages = "com.events")
@EventHandlerScan(basePackages = "com.event.handlers")
@AggregateEventHandlerScan(basePackages = "com.aggregate.event.handlers")
public class AppEventSourcingEnabledWithCustomPackages {
}
