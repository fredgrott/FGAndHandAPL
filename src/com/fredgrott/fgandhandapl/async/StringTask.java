package com.fredgrott.fgandhandapl.async;

import com.fredgrott.fgandhandapl.R;

import android.content.res.Resources;

public final class StringTask extends TaskBase<Long, String> {

    /* UI Thread */
    public StringTask(Resources resources, Long[] args) {
	super(resources, args);
    }

    /* Separate Thread */
    @Override
    protected String doInBackground(Long... args) {
	// Working in separate thread
	for (Long i = args[0]; i > 0; --i)
	{
	    // Check if task is cancelled
	    if (isCancelled()) {
		// This return causes onPostExecute call on UI thread
		return i.toString();
	    }

	    try {
		// This call causes onProgressUpdate call on UI thread
		publishProgress(mResources.getString(R.string.task_working, i));
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
		// This return causes onPostExecute call on UI thread
		return i.toString();
	    }
	}

	// This return causes onPostExecute call on UI thread
	return "complete";
    }
}