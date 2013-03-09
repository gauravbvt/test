package com.mindalliance.channels.guide;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Guide panel interface.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/8/13
 * Time: 10:00 PM
 */
public interface IGuidePanel {

    void selectTopicInSection( String sectionId, String topicId, AjaxRequestTarget target );

}
