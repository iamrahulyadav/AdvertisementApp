package com.projects.owner.camlocation.Utils;

import android.os.Handler;
import android.os.HandlerThread;

public class NonUiWorkerThread extends HandlerThread
{
    private Handler mWorkerHandler;

    public NonUiWorkerThread(String name) {
        super(name);
    }

    public NonUiWorkerThread(String name, int priority) {
        super(name, priority);
    }

    public void postTask(Runnable task)
    {
        mWorkerHandler.post(task);

    }

    public void prepareHandler()
    {
        mWorkerHandler = new Handler((getLooper()));
    }
}
