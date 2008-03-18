package com.mindalliance.channels.playbook;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Homepage
 */
public class HomePage extends Template {

	private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
        super( parameters );

        // Add the simplest type of label
        add( new Label("title", "Playbook - Index") );
        add( new Label("message", "Hello!") );
   }
}
