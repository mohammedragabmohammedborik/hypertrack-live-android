package io.hypertrack.sendeta.store.callback;

/**
 * Created by ulhas on 19/06/16.
 */
public abstract class UserStoreGetTaskCallback {
    public abstract void OnSuccess(String taskID, String hypertrackDriverID, String publishableKey);
    public abstract void OnError();
}
