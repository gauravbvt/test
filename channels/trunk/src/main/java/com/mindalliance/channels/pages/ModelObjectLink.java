package com.mindalliance.channels.pages;

import com.mindalliance.channels.ModelObject;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 12:40:26 PM
 */
public class ModelObjectLink extends ExternalLink {

    public ModelObjectLink( String s, String s1 ) {
        super( s, s1 );
    }

    public static IModel<String> linkFor( ModelObject mo) {
        return null; // TODO
    }
}
