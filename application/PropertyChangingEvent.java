/**
 * Helper class to trigger event when property is about to change
 * @author Greg Edwards
 * @version 1.0
 */
 
 package application;

import java.beans.PropertyChangeEvent;

public class PropertyChangingEvent extends PropertyChangeEvent
{
	public PropertyChangingEvent(Object source, String propertyName, Object oldValue, Object newValue)
	{
		super(source, propertyName, oldValue, newValue); 
	}
}