package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Indicator aware Web markup container.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/5/11
 * Time: 3:49 PM
 */
public class IndicatorAwareWebContainer extends WebMarkupContainer implements IAjaxIndicatorAware {

    private final String indicatorId;

    public IndicatorAwareWebContainer( String id, String indicatorId ) {
        super( id );
        this.indicatorId = indicatorId;
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicatorId;
    }
}
