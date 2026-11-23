package co.edu.unicauca.lisw2t5g02.presentation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

/** Runs REST calls outside the Swing thread and reports back on it. */
public final class Background {

    private Background() {
    }

    public static <T> void run(
            Callable<T> work,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {

        new SwingWorker<T, Void>() {

            @Override
            protected T doInBackground() throws Exception {
                return work.call();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    onError.accept(cause instanceof Exception error
                            ? error
                            : new RuntimeException(cause));
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
