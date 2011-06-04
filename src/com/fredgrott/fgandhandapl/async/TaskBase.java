package com.fredgrott.fgandhandapl.async;

import com.fredgrott.fgandhandapl.R;

import android.content.res.Resources;
import android.os.AsyncTask;

public abstract class TaskBase<TArgs, TResult> extends AsyncTask<TArgs, String ,TResult>  {

	protected final Resources mResources;

    private TArgs[] mArgs;
    private TResult mResult;
    private String mProgressMessage;
    private IProgressTracker mProgressTracker;

    /* UI Thread */
    public TaskBase(Resources resources) {
	this (resources, null);
    }

    /* UI Thread */
    public TaskBase(Resources resources, TArgs[] args) {
	// Keep reference to resources
	mResources = resources;
	// Keep arguments
	mArgs = args;
	// Initialize initial pre-execute message
	mProgressMessage = resources.getString(R.string.task_starting);
    }

    /* UI Thread */
    public void executeNonGenericWay() {
	// Allow AsyncTaskManager to call this method with executing real one
	execute(mArgs);
    }

    /* UI Thread */
    public void setProgressTracker(IProgressTracker progressTracker) {
	// Attach to progress tracker
	mProgressTracker = progressTracker;
	// Initialise progress tracker with current task state
	if (mProgressTracker != null) {
	    mProgressTracker.onProgress(mProgressMessage);
	    if (mResult != null) {
		mProgressTracker.onComplete();
	    }
	}
    }

    /* UI Thread */
    @Override
    protected void onCancelled() {
	// Detach from progress tracker
	mProgressTracker = null;
    }

    /* UI Thread */
    @Override
    protected void onProgressUpdate(String... values) {
	// Update progress message
	mProgressMessage = values[0];
	// And send it to progress tracker
	if (mProgressTracker != null) {
	    mProgressTracker.onProgress(mProgressMessage);
	}
    }

    /* UI Thread */
    @Override
    protected void onPostExecute(TResult result) {
	// Update result
	mResult = result;
	// And send it to progress tracker
	if (mProgressTracker != null) {
	    mProgressTracker.onComplete();
	}
	// Detach from progress tracker
	mProgressTracker = null;
    }

}
