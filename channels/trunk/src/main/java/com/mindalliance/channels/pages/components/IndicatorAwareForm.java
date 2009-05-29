package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.html.form.Form;

/**
 * Ajax indicator aware form.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2009
 * Time: 9:35:06 AM
 */
public class IndicatorAwareForm extends Form<Object> implements IAjaxIndicatorAware {

    public IndicatorAwareForm( String id ) {
        super( id );
    }

    public String getAjaxIndicatorMarkupId() {
        return "spinner";
    }
}
