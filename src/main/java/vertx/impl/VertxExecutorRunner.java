package vertx.impl;

import io.vertx.core.Vertx;

import java.util.concurrent.Executor;

public class VertxExecutorRunner
{
    public static void main(String[] args)
    {
        Executor executor = new VertxExecutorImpl(Vertx.vertx());
        executor.execute(() -> System.out.println("Executing Task"));
    }
}
