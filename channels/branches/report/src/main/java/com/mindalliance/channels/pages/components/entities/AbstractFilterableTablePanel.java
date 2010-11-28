package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Filterable abstract table panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 30, 2009
 * Time: 2:59:43 PM
 */
abstract public class AbstractFilterableTablePanel extends AbstractTablePanel implements Filterable {
    /**
     * Filters.
     */
    private Map<String, Identifiable> filters = new HashMap<String, Identifiable>();

    public AbstractFilterableTablePanel( String id ) {
        super( id );
    }

    public AbstractFilterableTablePanel( String id, int pageSize ) {
        super( id, null, pageSize, null );
    }

    public AbstractFilterableTablePanel(
            String id,
            IModel<? extends Identifiable> iModel,
            int pageSize,
            Set<Long> expansions ) {
        super( id, iModel, pageSize, expansions );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, identifiable );
        }
        resetTable( target );
    }

    abstract protected void resetTable( AjaxRequestTarget target );

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        Identifiable i = filters.get( property );
        return i != null && i.equals( identifiable );
    }

    /**
     * Whether a bean is filtered out.
     *
     * @param bean an object
     * @return a boolean
     */
    protected boolean isFilteredOut( Object bean ) {
        for ( String property : filters.keySet() ) {
            if ( !ModelObject.areEqualOrNull(
                    (ModelObject) filters.get( property ),
                    (ModelObject) ChannelsUtils.getProperty( bean, property, null ) ) ) {
                return true;
            }
        }
        return false;
    }

}
