package datatypes;

import application.FileTransferController;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

public class ProgressUpdate2 {

	
	public static void startProgressTask(ProgressBar progressBar) {
		final double EPSILON = 0.0000005;
	final Task<Void> task = new Task<Void>() {
        final int N_ITERATIONS = 100;

        @Override
        protected Void call() throws Exception {
            for (int i = 0; i < N_ITERATIONS; i++) {
                updateProgress(i + 1, N_ITERATIONS);
                // sleep is used to simulate doing some work which takes some time....
                Thread.sleep(100);
            }

            return null;
        }
    };

    progressBar.progressProperty().bind(
            task.progressProperty()
    );
    // color the bar green when the work is complete.
    progressBar.progressProperty().addListener(observable -> {
        if (progressBar.getProgress() >= 1 - EPSILON) {
            progressBar.setStyle("-fx-accent: forestgreen;");
        }
    });

    final Thread thread = new Thread(task, "task-thread");
    thread.setDaemon(true);
    thread.start();
	}
}

