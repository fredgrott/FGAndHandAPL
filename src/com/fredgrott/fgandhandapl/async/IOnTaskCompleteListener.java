package com.fredgrott.fgandhandapl.async;

public interface IOnTaskCompleteListener {

	// Notifies about task completeness
    void onTaskComplete(TaskBase<?,?> task);

}
