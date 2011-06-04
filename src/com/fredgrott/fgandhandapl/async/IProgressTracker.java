package com.fredgrott.fgandhandapl.async;

public interface IProgressTracker {

	// Updates progress message
    void onProgress(String message);
    // Notifies about task completeness
    void onComplete();

}
