package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.FilterTree;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.swing.tree.DefaultTreeModel;

/**
 * ...
 */
public class FilterPanel extends Panel {

    public FilterPanel( String id, IModel model ) {
        super( id, model );

        final Button applyButton = new Button( "filter-apply" );

        // Todo disable button on javascript-disabled clients only
        // something like:
        // applyButton.add( new SimpleAttributeModifier( "onload", "this.disabled=true;") );
        // meanwhile...
        applyButton.setEnabled( false );


        final Form form = new Form( "filter-form" ){
            protected void onSubmit() {
                super.onSubmit();
                applyButton.setEnabled( false );
                getTab().commit();
                onFilterApplied();
            }
        };

        final FilterTree tree = new FilterTree( "filter-tree", new DefaultTreeModel( getTab().getFilter() ) ){
            public void onCheckBoxUpdate( AjaxRequestTarget target, Filter filter ) {
                applyButton.setEnabled( true );
                target.addComponent( form );
                target.addComponent( applyButton );
            }
        };
        tree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );

        form.add( tree );
        form.add( applyButton );
        add( form );
    }

    public Tab getTab() {
        return (Tab) ((Ref) getModelObject() ).deref();
    }

    /**
     * Override this for specific page behaviors.
     */
    public void onFilterApplied(){}
}
