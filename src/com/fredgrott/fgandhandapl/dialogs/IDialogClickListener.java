package com.fredgrott.fgandhandapl.dialogs;

/**
 * Adapted from DroidFU
 * @author FredGrott
 *
 * @param <T>
 */
public interface IDialogClickListener<T> {

	void onClick(int index, T element);

}
