/**
 * Interface for an object to notify subscribers of changes
 * @author Greg Edwards
 * @version 1.0
 */
 
package application;

import java.beans.PropertyChangeListener;

public interface IListenable
{
	/**
	 * Add a PropertyChangeListener to the listener list
	 * @param listener the PropertyChangeListen to add
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Remove a PropertyChangeListener from the listener list
	 * @param listener the PropertyChangeListener to add
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
}