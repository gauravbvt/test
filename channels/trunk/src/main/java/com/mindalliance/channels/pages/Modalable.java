package com.mindalliance.channels.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/4/12
 * Time: 7:51 PM
 */
public interface Modalable {

    public void addModalDialog( String id, String cookieName, MarkupContainer container );

    void showDialog( String title,
                     int height,
                     int width,
                     Updatable contents,
                     Updatable updatable,
                     AjaxRequestTarget target );

    void hideDialog( AjaxRequestTarget target );

    String getModalContentId();
}
