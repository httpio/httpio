package com.httpio.app.services;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.util.Duration;
import org.controlsfx.control.StatusBar;

import java.util.HashMap;
import java.util.Map;

public class TasksSupervisor {
    private final HashMap<Task, Number> tasks = new HashMap<>();

    private StatusBar statusBar;

    public TasksSupervisor() {
        ScheduledService scheduledService = new ScheduledService() {
            @Override
            protected Task createTask() {
            return new Task() {
                @Override
                protected Object call() {
                    reload();

                    return null;
                }
            };
            }
        };

        scheduledService.setPeriod(Duration.seconds(1));
        scheduledService.start();
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    private void reload() {
        if (statusBar != null) {
            if (tasks.size() > 0) {
                double sum = 0;

                for(Map.Entry<Task, Number> entry: tasks.entrySet()) {
                    sum += entry.getValue().doubleValue();
                }

                statusBar.setProgress(sum / tasks.size());
            } else {
                statusBar.setProgress(0);
            }
        }
    }

    public void start(Task t) {
        synchronized (tasks) {
            final Task task = t;

            task.progressProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number number, Number t1) {
                    tasks.put(task, t1);
                }
            });

            task.stateProperty().addListener(new ChangeListener<Task.State>() {
                @Override
                public void changed(ObservableValue<? extends Task.State> observableValue, Task.State state, Task.State t1) {
                    if (t1 != Task.State.RUNNING) {
                        tasks.remove(task);
                    }
                }
            });

            Thread taskThread = new Thread(task);

            taskThread.start();
        }
    }
}
