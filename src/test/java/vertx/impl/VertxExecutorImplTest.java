package vertx.impl;

import io.vertx.core.Vertx;

import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(VertxExtension.class)
public class VertxExecutorImplTest
{
    private Vertx vertx;

    @BeforeEach
    public void setup()
    {
        vertx = mock(Vertx.class);
    }

    @Test
    public void executeArrivedInVertxContext()
    {
        Executor vertxExecutor = new VertxExecutorImpl(vertx);
        Runnable mockRunnable = mock(Runnable.class);


    }

}
