package io.aitech.pv.util;

/**
 * Represents an operation that does not return a result.
 *
 * <p> This is a {@linkplain java.util.function functional interface}
 * whose functional method is {@link #run()}.
 *
 * @since 1.0
 */
@FunctionalInterface
public interface URunnable {
    /**
     * Runs this operation.
     */
    void run() throws Throwable;
}
