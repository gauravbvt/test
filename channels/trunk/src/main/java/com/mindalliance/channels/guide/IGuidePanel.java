package com.mindalliance.channels.guide;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.Map;

/**
 * Guide panel interface.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/8/13
 * Time: 10:00 PM
 */
public interface IGuidePanel {

    /**
     * Set the context for resolving property paths.
     * @param context named objects
     */
    void setContext( Map<String,Object> context );

    void selectTopicInSection( String sectionId, String topicId, AjaxRequestTarget target );

}
