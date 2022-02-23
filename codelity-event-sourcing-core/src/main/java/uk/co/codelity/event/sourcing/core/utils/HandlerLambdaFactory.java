package uk.co.codelity.event.sourcing.core.utils;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.BiConsumer;

public class HandlerLambdaFactory {
    private HandlerLambdaFactory() {
    }

    public static <T, U> BiConsumer<T, U> createHandlerLambda(Class<T> aggregateType,
                                                              Class<U> eventType,
                                                              String methodName) throws Throwable {

        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodHandle target = caller.findVirtual(aggregateType, methodName,
                MethodType.methodType(void.class, eventType));
        MethodType instantiated = target.type().wrap().changeReturnType(void.class);

        CallSite site = LambdaMetafactory.metafactory(caller,
                "accept", MethodType.methodType(BiConsumer.class),
                instantiated.erase(), target, instantiated);
        return (BiConsumer<T, U>)site.getTarget().invoke();
    }

}
