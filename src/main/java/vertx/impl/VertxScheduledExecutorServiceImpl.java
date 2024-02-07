package vertx.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class VertxScheduledExecutorServiceImpl implements ScheduledExecutorService
{
    private final VertxExecutorServiceImpl executorService;

    public VertxScheduledExecutorServiceImpl(VertxExecutorServiceImpl executorService)
    {
        this.executorService = executorService;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
    {
        if (command == null) throw new NullPointerException();

        CompletableFuture<?> future = new CompletableFuture<>();
        long delayInMillis = unit.toMillis(delay);

        executorService.getVertx().setTimer(delayInMillis, timerId ->
        {
            try
            {
                executorService.execute(command);
                future.complete(null);
            } catch (Throwable t)
            {
                future.completeExceptionally(t);
            }
        });

        // Need to implement scheduled future itself?
        return null;
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
    {
        if (callable == null) throw new NullPointerException();

        CompletableFuture<V> future = new CompletableFuture<>();
        long delayInMillis = unit.toMillis(delay);

        executorService.getVertx().setTimer(delayInMillis, timerId ->
        {
            try
            {
                future.complete(callable.call());
            } catch (Throwable t)
            {
                future.completeExceptionally(t);
            }
        });

        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
    {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
    {
        return null;
    }

    @Override
    public void execute(Runnable command)
    {
        executorService.execute(command);
    }

    @Override
    public void shutdown()
    {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow()
    {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown()
    {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated()
    {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result)
    {
        return executorService.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task)
    {
        return executorService.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        return executorService.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException
    {
        return executorService.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return executorService.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        return executorService.invokeAny(tasks, timeout, unit);
    }
}
