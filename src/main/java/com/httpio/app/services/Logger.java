package com.httpio.app.services;

import javafx.scene.control.Alert;
import javafx.scene.text.Text;

public class Logger {
    Text statusBar;

    int clearTimeout = 0;

    Thread threadCleaner;

    public Logger() {
        initCleaner();
    }

    private void initCleaner() {
        Thread threadCleaner = new Thread() {
            @Override
            public void run() {
                super.run();

                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (statusBar == null) {
                        continue;
                    }

                    if (statusBar.getText() == null) {
                        continue;
                    }

                    clearTimeout--;

                    if (clearTimeout <= 0) {
                        statusBar.setText(null);
                    }
                }
            }
        };

        threadCleaner.start();
    }

    public void setStatusBar(Text statusBar) {
        this.statusBar = statusBar;
    }

    public void log(String message) {
        log(Levels.LOG, message);
    }

    public void log(Levels level, String message) {
        if (statusBar != null) {
            statusBar.setText(message);

            clearTimeout = 5;
        }

        if (level == Levels.ERROR) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            // alert.setTitle("Open project error");
            // alert.setHeaderText(" error");
            alert.setContentText(message);

            alert.showAndWait();
        }
    }

    public enum Levels {
        ERROR,
        WARNING,
        NOTICE,
        LOG
    };
}