package com.mindalliance.configuration;

public class UIAutomationException extends Exception{
	/**
	 * 
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
