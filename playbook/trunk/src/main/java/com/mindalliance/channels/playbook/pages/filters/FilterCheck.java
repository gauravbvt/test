package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class FilterCheck extends Panel {

    private AjaxCheckBox checkBox;

    public AjaxCheckBox getCheckBox() {
        return checkBox;
    }

    public FilterCheck( String id, IModel model ) {
        super( id );
        setRenderBodyOnly( true );

        checkBox = new AjaxCheckBox( "filter-check", model ){
            protected void onUpdate( AjaxRequestTarget target ) {
                onFilterSelect( target );
            }
        };
        checkBox.setOutputMarkupId( true );

        add( checkBox );
    }

    public void onFilterSelect( AjaxRequestTarget target ) {
    }
}
