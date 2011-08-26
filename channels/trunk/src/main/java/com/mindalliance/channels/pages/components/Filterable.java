package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.model.Identifiable;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Something that manages a filter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 4:18:30 PM
 */
public interface Filterable {

    /**
     * Add or remove a property-qualified filter.
     *
     * @param identifiable an identifiable
     * @param property     a string
     * @param target       an ajax request target
     */
    void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target );

    /**
     * Whether an identifiable is a filter.
     *
     * @param identifiable an identifiable
     * @param property     property the identifiable is filtered in
     * @return a boolean
     */
    boolean isFiltered( Identifiable identifiable, String property );
}
