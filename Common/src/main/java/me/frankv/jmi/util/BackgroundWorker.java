package me.frankv.jmi.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Runs jobs on a single daemon thread and hands results back to whichever thread calls {@link #drain}.
 */
public final class BackgroundWorker<J, R> {

    public record Result<J, R>(J job, R value, Throwable error) {
    }

    private final ExecutorService executor;
    private final Function<J, R> compute;
    private final Queue<Result<J, R>> results = new ConcurrentLinkedQueue<>();

    public BackgroundWorker(String threadName, Function<J, R> compute) {
        this.compute = compute;
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            final var thread = new Thread(runnable, threadName);
            thread.setDaemon(true);
            return thread;
        });
    }

    public void submit(J job) {
        executor.execute(() -> {
            try {
                results.add(new Result<>(job, compute.apply(job), null));
            } catch (Throwable t) {
                results.add(new Result<>(job, null, t));
            }
        });
    }

    public void drain(Consumer<Result<J, R>> onResult) {
        Result<J, R> result;
        while ((result = results.poll()) != null) {
            onResult.accept(result);
        }
    }
}
