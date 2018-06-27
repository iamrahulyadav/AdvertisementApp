package com.projects.owner.camlocation;

/**
 * Created by owner on 10/08/2015.
 */
public interface IWebServiceHandler {
    void StartedRequest();

    void CodeFinished(String methodName, Object Data);

    void CodeFinishedWithException(Exception ex, String exp);

    void CodeEndedRequest();
}