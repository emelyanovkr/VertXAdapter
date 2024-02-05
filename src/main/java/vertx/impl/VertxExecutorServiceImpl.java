package vertx.impl;

import io.vertx.core.Vertx;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class VertxExecutorServiceImpl implements ExecutorService
{
    private final VertxExecutorImpl vertxExecutor;
    private final Vertx vertx;
    private boolean isShutdown;
    private boolean isTerminated;

    public VertxExecutorServiceImpl(VertxExecutorImpl vertxExecutor)
    {
        this.vertxExecutor = vertxExecutor;
        vertx = vertxExecutor.getVertx();

        isShutdown = false;
        isTerminated = false;
    }

    @Override
    public void execute(Runnable command)
    {
        if(command == null) throw new NullPointerException();
        vertxExecutor.execute(command);

    }

    @Override
    public void shutdown()
    {
        vertx.close();
        isTerminated = true;
    }

    @Override
    public List<Runnable> shutdownNow()
    {
        return null;
    }

    @Override
    public boolean isShutdown()
    {
        return isShutdown;
    }

    @Override
    public boolean isTerminated()
    {
        return isTerminated;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
    {
        // temporary
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        if (task == null) throw new NullPointerException();
        CompletableFuture<T> future = new CompletableFuture<>();

        // why lambda parameter was called ignoredEvent ???
        vertx.runOnContext(v ->
        {
            try {
                future.complete(task.call());
            } catch (Throwable t)
            {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result)
    {
        if (task == null) throw new NullPointerException();

        CompletableFuture<T> future = new CompletableFuture<>();

        vertx.runOnContext(v ->
        {
            try
            {
                task.run();
                future.complete(result);
            } catch (Throwable t)
            {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    @Override
    public Future<?> submit(Runnable task)
    {
        if (task == null) throw new NullPointerException();

        // what's the point of returning Future with Runnable task (no result returned) ???
        CompletableFuture<?> future = new CompletableFuture<>();

        vertx.runOnContext(v ->
        {
            try
            {
                task.run();
                future.complete(null);
            } catch (Throwable t)
            {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        throw new UnsupportedOperationException();
    }
}
