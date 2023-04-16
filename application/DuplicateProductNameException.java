/**
 * Thrown when a duplicate product name appeasrs in inventory
 * @author Greg Edwards
 * @version 1.0
 */
package application;

 public class DuplicateProductNameException extends RuntimeException
 {
	public String _duplicateName;
	
	public DuplicateProductNameException(String duplicateName)
	{
		_duplicateName = duplicateName;
	}
	
	public String getDuplicateName()
	{
		return _duplicateName;
	}
 }