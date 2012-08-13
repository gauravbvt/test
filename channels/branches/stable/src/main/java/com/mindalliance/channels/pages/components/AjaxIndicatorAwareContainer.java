package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Indicator aware container.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/24/11
 * Time: 2:03 PM
 */
public class AjaxIndicatorAwareContainer extends WebMarkupContainer implements IAjaxIndicatorAware {

    private String indicatorId;

    public AjaxIndicatorAwareContainer( String id, String indicatorId ) {
        super( id );
        this.indicatorId = indicatorId;
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicatorId;
    }
}
