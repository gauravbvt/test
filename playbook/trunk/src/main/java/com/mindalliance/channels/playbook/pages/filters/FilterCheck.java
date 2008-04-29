package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class FilterCheck extends Panel {

    public FilterCheck( String id, IModel model ) {
        super( id );
        setRenderBodyOnly( true );

        final AjaxCheckBox checkBox = new AjaxCheckBox( "filter-check", model ){
            protected void onUpdate( AjaxRequestTarget target ) {
                onFilterSelect( target, (Filter) FilterCheck.this.getModelObject() );
            }
        };
        checkBox.setOutputMarkupId( true );

        add( checkBox );
    }

    public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {        
    }
}
