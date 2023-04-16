/**
 * Helper class to trigger event when property has changed
 * @author Greg Edwards
 * @version 1.0
 */
 
 package application;

import java.beans.PropertyChangeEvent;

public class PropertyChangedEvent extends PropertyChangeEvent
{
	public PropertyChangedEvent(Object source, String propertyName, Object oldValue, Object newValue)
	{
		super(source, propertyName, oldValue, newValue); 
	}
}