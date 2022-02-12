package uk.co.codelity.event.sourcing.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation, which tells the packages to be scanned for @Event annotated classes.
 * This annotation must be present in the same class with @EventSourcingEnabled annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventScan {
    String[] basePackages() default {};
}
