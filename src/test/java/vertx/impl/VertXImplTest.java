package vertx.impl;

import io.vertx.core.Vertx;

import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class VertXImplTest
{
    private volatile AtomicInteger task_number;
    private VertxExecutorServiceImpl executorService;

    @BeforeEach
    public void InitExecutorService()
    {
        // Make a fabric of something for a better instance creation
        executorService = new VertxExecutorServiceImpl(new VertxExecutorImpl(Vertx.vertx()));
        task_number = new AtomicInteger(0);
    }

    @Test
    public void AwaitTerminationReturnsTrueAfterShutdown()
    {
        executorService.shutdown();
        assertTrue(executorService.isShutdown());
        try
        {
            assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void AwaitTerminationReturnsFalseTimeoutExceeded()
    {
        Runnable blockingTask = () ->
        {
            synchronized (this)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };

        executorService.submit(blockingTask);

        assertFalse(executorService.isTerminated());
        executorService.shutdown();

        try
        {
            assertFalse(executorService.awaitTermination(1, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        assertFalse(executorService.isTerminated());
    }

    @Test
    public void SeveralFuturesReturnCorrectResults()
    {
        task_number.set(0);

        // Somehow there is only one thread working
        Future<String> future_one = executorService.submit(() ->
        {
            System.out.println("Task is running " + task_number.incrementAndGet());

            System.out.println("Going to sleep " + Thread.currentThread().getClass().getSimpleName());
            Thread.sleep(1500);
            System.out.println("Waking up " + Thread.currentThread().getClass().getSimpleName());

            return "Task #" + task_number + " returning";
        });

        Future<String> future_two = executorService.submit(() ->
        {
            System.out.println("Task is running " + task_number.incrementAndGet());

            return "Task #" + task_number + " returning";
        });

        Future<String> future_three = executorService.submit(() ->
        {
            System.out.println("Task is running " + task_number.incrementAndGet());

            System.out.println("Going to sleep " + Thread.currentThread().getClass().getSimpleName());
            Thread.sleep(500);
            System.out.println("Waking up " + Thread.currentThread().getClass().getSimpleName());

            return "Task #" + task_number + " returning";
        });

        try
        {
            assertEquals(future_one.get(), "Task #1 returning" );
            assertEquals(future_two.get(), "Task #2 returning" );
            assertEquals(future_three.get(), "Task #3 returning" );
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        finally
        {
            executorService.shutdown();
        }
    }

    @Test
    public void SubmitReturnPassedResult()
    {
        List<Runnable> runnableList = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            int finalI = i;
            runnableList.add(() -> System.out.println("Runnable task #" + finalI));
        }

        List<Future<Integer>> futures = new ArrayList<>();

        for(Runnable r : runnableList)
        {
            futures.add(executorService.submit(r, task_number.incrementAndGet()));
        }

        for(int i = 0; i < 5; ++i)
        {
            Future<Integer> f = futures.get(i);

            try
            {
                assertEquals(f.get(), i + 1);
            } catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            } finally
            {
                executorService.shutdown();
            }
        }
    }
}
