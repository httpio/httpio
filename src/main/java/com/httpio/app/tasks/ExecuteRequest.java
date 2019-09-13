package com.httpio.app.tasks;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.services.HTTPSender;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ExecuteRequest extends Task<Void> {
    private Request request;
    private Profile profile;
    private HTTPSender httpSender;

    public ExecuteRequest(Request request, Profile profile, HTTPSender httpSender) {
        this.request = request;
        this.profile = profile;
        this.httpSender = httpSender;
    }

    @Override
    protected Void call() throws Exception {
        updateProgress(1, 3);

        HTTPSender.Response response = httpSender.send(request, profile);

        updateProgress(2, 3);

        // Platform.runLater(new Runnable);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                request.setLastResponse(response);
            }
        });

        updateProgress(3, 3);

        return null;
    }
}
