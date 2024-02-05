package vertx.impl;

import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class VertxExecutorRunner
{
    private static int task_number = 0;

    public static void main(String[] args)
    {
        VertxExecutorServiceImpl vertxExecutorService = new VertxExecutorServiceImpl(new VertxExecutorImpl(Vertx.vertx()));

        Future<String> future_before = vertxExecutorService.submit(() ->
        {
            System.out.println("Task is running " + ++task_number * 3);

            System.out.println("Going to sleep" + Thread.currentThread().getName());
            Thread.sleep(4500);
            System.out.println("Waking up" + Thread.currentThread().getName());

            return "Task #" + task_number * 3 + " returning";
        });

        Future<String> future = vertxExecutorService.submit(() ->
        {
            System.out.println("Task is running " + ++task_number);

            return "Task #" + task_number + " returning";
        });

        Future<String> future_one = vertxExecutorService.submit(() ->
        {
            System.out.println("Task is running " + ++task_number * 2);

            System.out.println("Going to sleep" + Thread.currentThread().getName());
            Thread.sleep(3000);
            System.out.println("Waking up" + Thread.currentThread().getName());

            return "Task #" + task_number * 2 + " returning";
        });

        try
        {
            System.out.println(future_before.get());
            System.out.println(future.get());
            System.out.println(future_one.get());
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            vertxExecutorService.shutdown();
        }


        // Checking submit implementation with providing result parameter
        VertxExecutorServiceImpl executorService = new VertxExecutorServiceImpl(new VertxExecutorImpl(Vertx.vertx()));

        List<Runnable> runnableList = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            int finalI = i;
            runnableList.add(() -> System.out.println("Runnable task #" + finalI));
        }

        List<Future<Integer>> futures = new ArrayList<>();

        for(Runnable r : runnableList)
        {
            futures.add(executorService.submit(r, ++task_number));
        }

        for(Future f : futures)
        {
            try
            {
                System.out.println(f.get());
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                executorService.shutdown();
            }
        }
    }
}
