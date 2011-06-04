package com.fredgrott.fgandhandapl.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * The idea is correctly resume AsyncTask upon a
 * device orientation change. To do that one detaches the
 * AsyncTask from the GUI view context to retain the object
 * in order to resume the task upon the orientation change.
 *
 * Activity context is always a view context and thus we detach in
 * order to retain the asynctask progress, etc upon orientation
 * change.
 *
 * We use this by:
 *
 * <code>private AsyncTaskManager mAsyncTaskManager;
 *
 *   @Override
 *   public void onCreate(Bundle savedInstanceState) {
 *	super.onCreate(savedInstanceState);
 *	setContentView(R.layout.main);
 *
 *	// Create manager and set this activity as context and listener
 *	mAsyncTaskManager = new AsyncTaskManager(this, this);
 *	// Handle task that can be retained before
 *	mAsyncTaskManager.handleRetainedTask(getLastNonConfigurationInstance());
 *   }
 *
 *   public void onRunBooleanTaskButtonClick(View view) {
 *	// Create and run task and progress dialog
 *	mAsyncTaskManager.setupTask(new BooleanTask(getResources()));
 *   }
 *
 *   public void onRunStringTaskButtonClick(View view) {
 *	// Create and run task and progress dialog
 *	mAsyncTaskManager.setupTask(new StringTask(getResources(), new Long[] { 15L }));
 *   }
 *
 *    @Override
 *    public Object onRetainNonConfigurationInstance() {
 *	// Delegate task retain to manager
 *	return mAsyncTaskManager.retainTask();
 *  }
 *
 *   @Override
 *   public void onTaskComplete(TaskBase<?,?> task) {
 *	if (task.isCancelled()) {
 *	    // Report about cancel
 *	    Toast.makeText(this, R.string.task_cancelled, Toast.LENGTH_LONG)
 *		    .show();
 *	} else {
 *	    // Get result
 *	    Object result = null;
 *	    try {
 *		result = task.get();
 *	    } catch (Exception e) {
 *		e.printStackTrace();
 *	    }
 *	    // Report about result
 *	    Toast.makeText(this,
 *		    getString(
 *			R.string.task_completed,
 *			(result != null) ? result.toString() : "null",
 *			result.getClass().toString()), Toast.LENGTH_LONG).show();
 *	}
 *   }
 * </code>
 *
 *
 *
 * @author FredGrott
 *
 */
public final class AsyncTaskManager implements IProgressTracker, OnCancelListener {

    private final IOnTaskCompleteListener mTaskCompleteListener;
    private final ProgressDialog mProgressDialog;
    private TaskBase<?,?> mAsyncTask;

    public AsyncTaskManager(Context context, IOnTaskCompleteListener taskCompleteListener) {
	// Save reference to complete listener (activity)
	mTaskCompleteListener = taskCompleteListener;
	// Setup progress dialog
	mProgressDialog = new ProgressDialog(context);
	mProgressDialog.setIndeterminate(true);
	mProgressDialog.setCancelable(true);
	mProgressDialog.setOnCancelListener(this);
    }

    public void setupTask(TaskBase<?,?> asyncTask) {
	// Keep task
	mAsyncTask = asyncTask;
	// Wire task to tracker (this)
	mAsyncTask.setProgressTracker(this);
	// Start task
	mAsyncTask.executeNonGenericWay();
    }

    @Override
    public void onProgress(String message) {
	// Show dialog if it wasn't shown yet or was removed on configuration (rotation) change
	if (!mProgressDialog.isShowing()) {
	    mProgressDialog.show();
	}
	// Show current message in progress dialog
	mProgressDialog.setMessage(message);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
	// Cancel task
	mAsyncTask.cancel(true);
	// Notify activity about completion
	mTaskCompleteListener.onTaskComplete(mAsyncTask);
	// Reset task
	mAsyncTask = null;
    }

    @Override
    public void onComplete() {
	// Close progress dialog
	mProgressDialog.dismiss();
	// Notify activity about completion
	mTaskCompleteListener.onTaskComplete(mAsyncTask);
	// Reset task
	mAsyncTask = null;
    }

    public Object retainTask() {
	// Detach task from tracker (this) before retain
	if (mAsyncTask != null) {
	    mAsyncTask.setProgressTracker(null);
	}
	// Retain task
	return mAsyncTask;
    }

    public void handleRetainedTask(Object instance) {
	// Restore retained task and attach it to tracker (this)
	if (instance instanceof TaskBase<?,?>) {
	    mAsyncTask = (TaskBase<?,?>) instance;
	    mAsyncTask.setProgressTracker(this);
	}
    }

    public boolean isWorking() {
	// Track current status
	return mAsyncTask != null;
    }
}