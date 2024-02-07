package vertx.impl;

import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VertxExecutorRunner
{
    private volatile static AtomicInteger task_number = new AtomicInteger(0);

    public static void main(String[] args)
    {
        System.out.println(Thread.currentThread().getClass().getSimpleName());
    }
}
