package com.mindalliance.configuration;
/**
 * User defined exception are defined in this class
 */

public class UIAutomationException extends Exception{
	/**
	 * User defined exception are defined in this class
	 */
	private static final long serialVersionUID = 1L;
	public String errorMessage;
	public UIAutomationException(String message)
	{
		errorMessage	=	message;
	}
	public String toString()
	{
		return errorMessage;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}

}
