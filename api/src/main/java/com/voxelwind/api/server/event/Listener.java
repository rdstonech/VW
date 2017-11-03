package com.voxelwind.api.server.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation denotes that this method is an event handler. The method must accept one parameter, which is the
 * desired event class. Event listeners may be run asynchronously.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {
    int order() default 0;
}
