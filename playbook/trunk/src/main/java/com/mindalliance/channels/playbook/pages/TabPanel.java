package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * ...
 */
public class TabPanel extends Panel {

    public TabPanel( String id, IModel model ) {
        super( id, model );
        setRenderBodyOnly( true );

        final ContentPanel right = new ContentPanel( "tab-right", model );

        final WebMarkupContainer left = new WebMarkupContainer( "tab-left" );
        left.add( new FilterPanel( "filter", model ){
            public void onFilterApplied() {
//                left.renderComponent();
//                right.renderComponent();
            }
        } );

        add( new Label( "content-title", new RefPropertyModel( model, "name" ) ) );
        add( left );
        add( right );
    }
}
