package com.fredgrott.fgandhandapl.async;

import com.fredgrott.fgandhandapl.R;

import android.content.res.Resources;

public final class BooleanTask extends TaskBase<Void, Boolean> {

    /* UI Thread */
    public BooleanTask(Resources resources) {
	super(resources);
    }

    /* Separate Thread */
    @Override
    protected Boolean doInBackground(Void... args) {
	// Working in separate thread
	for (int i = 10; i > 0; --i)
	{
	    // Check if task is cancelled
	    if (isCancelled()) {
		// This return causes onPostExecute call on UI thread
		return false;
	    }

	    try {
		// This call causes onProgressUpdate call on UI thread
		publishProgress(mResources.getString(R.string.task_working, i));
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
		// This return causes onPostExecute call on UI thread
		return false;
	    }
	}
	// This return causes onPostExecute call on UI thread
	return true;
    }
}