package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class ResourcesPanel extends Panel {

    public ResourcesPanel( String id, IModel model ) {
        super( id, model );

        final RefDataProvider rawData = new RefDataProvider( model.getObject(), "resources" );
        final FilterPanel filter = new FilterPanel( "filter", rawData );
        add( filter );
        add( new ContentPanel( "contents", filter.getRawData() ) );
    }
}
