package com.httpio.app.tasks;

import com.httpio.app.models.Profile;
import com.httpio.app.models.Request;
import com.httpio.app.modules.RequestBridge;
import com.httpio.http.Response;
import com.httpio.http.Sender;
import com.httpio.http.impl.StandardRequest;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ExecuteRequest extends Task<Void> {
    private Request request;
    private Profile profile;
    private Sender sender;

    public ExecuteRequest(Request request, Profile profile, Sender sender) {
        this.request = request;
        this.profile = profile;
        this.sender = sender;
    }

    @Override
    protected Void call() {
        updateProgress(1, 3);

        final Response response = sender.send(new RequestBridge(request, profile));

        updateProgress(2, 3);

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
