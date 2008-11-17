package com.mindalliance.channels;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
public class WicketApplication extends WebApplication
{
    /**
     * Constructor
     */
	public WicketApplication()
	{
	}

	@Override
    public Class<ScenarioPage> getHomePage()
	{
		return ScenarioPage.class;
	}

}
