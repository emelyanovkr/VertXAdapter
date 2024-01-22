package vertx.impl;

import io.vertx.core.Vertx;

import java.util.concurrent.Executor;

public class VertxExecutorImpl implements Executor
{
    private final Vertx vertx;

    public VertxExecutorImpl(Vertx vertx)
    {
        this.vertx = vertx;
    }

    @Override
    public void execute(Runnable command)
    {
        vertx.runOnContext(v -> command.run());
    }
}
