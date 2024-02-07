package vertx.impl;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.core.impl.future.CompositeFutureImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

// Considering using extends for abstractions VertxExecutorServiceImpl
// extends VertxExecutor & Implements ExecutorService

// what is the best way to catch exceptions?

public class VertxExecutorServiceImpl implements ExecutorService
{
    private final VertxExecutorImpl vertxExecutor;
    private final Vertx vertx;
    private boolean isShutdown;
    private boolean isTerminated;

    // useless for now
    private boolean shutdownRequest;

    public VertxExecutorServiceImpl(VertxExecutorImpl vertxExecutor)
    {
        this.vertxExecutor = vertxExecutor;
        vertx = vertxExecutor.getVertx();

        shutdownRequest = false;
        isShutdown = false;
        isTerminated = false;
    }

    @Override
    public void execute(Runnable command)
    {
        if (command == null) throw new NullPointerException();
        vertxExecutor.execute(command);
    }

    @Override
    public void shutdown()
    {
        shutdownRequest = true;

        isShutdown = true;

        vertx.close(voidAsyncResult ->
        {
            if (voidAsyncResult.succeeded())
            {
                isTerminated = true;
            } else if (voidAsyncResult.failed())
            {
                try
                {
                    throw voidAsyncResult.cause();
                } catch (Throwable e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public List<Runnable> shutdownNow()
    {
        // temp
        throw new UnsupportedOperationException();
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
        long timeout_in_ms = unit.toMillis(timeout);
        long end_time = System.currentTimeMillis() + timeout_in_ms;

        while (!isTerminated && System.currentTimeMillis() < end_time)
        {
            Thread.sleep(100);
        }

        return isTerminated;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        if (task == null) throw new NullPointerException();
        CompletableFuture<T> future = new CompletableFuture<>();

        // why lambda parameter was called ignoredEvent ???
        vertx.runOnContext(v ->
        {
            try
            {
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
                execute(task);
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
                execute(task);
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
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<Future<T>> futures = new ArrayList<>();

        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException
    {
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<Future<T>> futures = new ArrayList<>();

        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try
        {
            allFutures.get(timeout, unit);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return futures;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<Future<T>> futures = new ArrayList<>();

        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }

        try
        {
            return CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).thenApply(result ->
            {
                try
                {
                    return (T) result;
                } catch (ClassCastException e)
                {
                    throw new CompletionException(e);
                }
            }).get();
        } catch (CompletionException e)
        {
            throw new ExecutionException(e);
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<Future<T>> futures = new ArrayList<>();

        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }

        try
        {
            return CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).thenApply(result ->
            {
                try
                {
                    return (T) result;
                } catch (ClassCastException e)
                {
                    throw new CompletionException(e);
                }
            }).orTimeout(timeout, unit).get();
        } catch (CompletionException e)
        {
            throw new ExecutionException(e);
        }
    }

    public Vertx getVertx()
    {
        return vertx;
    }

    public VertxExecutorImpl getVertxExecutor()
    {
        return vertxExecutor;
    }
}
