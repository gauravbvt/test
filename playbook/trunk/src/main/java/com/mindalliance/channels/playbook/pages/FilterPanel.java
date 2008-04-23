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

    private Filter filter;

    public FilterPanel( String id, IModel model ) {
        super( id, model );
        setFilter( getTab().getFilter() );

        final Button applyButton = new Button( "filter-apply" );

        // Todo disable button on javascript-disabled clients only
        // something like:
        // applyButton.add( new SimpleAttributeModifier( "onload", "this.disabled=true;") );
        // meanwhile...
        applyButton.setEnabled( false );

        final FilterTree tree = new FilterTree( "filter-tree", new DefaultTreeModel( getFilter() )){
            public void onCheckBoxUpdate( AjaxRequestTarget target, Filter filter ) {
                applyButton.setEnabled( true );
                target.addComponent( this );
                target.addComponent( applyButton );
            }

            public void onExpandCollapse( AjaxRequestTarget target, Filter filter ) {
            }
        };
        tree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );

        final Form form = new Form( "filter-form" ){
            protected void onSubmit() {
                super.onSubmit();
                applyButton.setEnabled( false );
                Tab tab = getTab();
                tab.persist();
                tab.setFilter( getFilter() );
                tab.commit();
                onFilterApplied( getFilter() );
            }
        };


        form.add( tree );
        form.add( applyButton );
        add( form );
    }

    public Tab getTab() {
        return (Tab) ((Ref) getModelObject() ).deref();
    }

    /**
     * Override this for specific page behaviors.
     * @param filter the filter that was actually applied
     */
    public void onFilterApplied( Filter filter ){}

    public final Filter getFilter() {
        return filter;
    }

    public final void setFilter( Filter filter ) {
        this.filter = filter;
    }
}
