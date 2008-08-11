package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class FilterCheck extends Panel {

    private static final long serialVersionUID = -2429186406303958407L;

    public FilterCheck( String id, IModel<Boolean> model ) {
        super( id );
        setRenderBodyOnly( true );

        AjaxCheckBox checkBox = new AjaxCheckBox( "filter-check", model ) {
            private static final long serialVersionUID = -4539076269840356122L;

            @Override
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
