package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.RootFilter;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class ResourcesPanel extends Panel {

    public ResourcesPanel( String id, IModel model ) {
        super( id, model );

        RefDataProvider rawData = new RefDataProvider( model.getObject(), "resources" );
        FilterPanel filterPanel = new FilterPanel( "filter",
            new RootFilter( Filter.Resources( rawData ) ), rawData );
        add( filterPanel );
        add( new ContentPanel( "contents", filterPanel.getFilteredData() ) );
    }
}
