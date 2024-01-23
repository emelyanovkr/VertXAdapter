package vertx.impl;

import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class VertxExecutorRunner
{
    public static void main(String[] args)
    {
        Executor executor = new VertxExecutorImpl(Vertx.vertx());
        executor.execute(() -> System.out.println("Executing Task"));

        List<Runnable> runnableList = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            int finalI = i;
            runnableList.add(() -> {
                System.out.println("Runnable task #" + finalI);
            });
        }

        for(Runnable r : runnableList)
        {
            executor.execute(r);
        }
    }
}
