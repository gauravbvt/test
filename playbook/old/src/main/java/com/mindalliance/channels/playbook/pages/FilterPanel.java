package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.FilterTree;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class FilterPanel extends Panel {

    private FilterTree tree;
    private static final long serialVersionUID = 4087476156321431707L;

    public FilterPanel( String id, IModel<Filter> filterModel ) {
        super( id, filterModel );

        final Button saveButton = new Button( "filter-save" ) {
            private static final long serialVersionUID = -2843911679384226175L;

            @Override
            public void onSubmit() {
                setEnabled(false);
                onFilterSave( getFilter() );
            }
        };
        saveButton.setEnabled( false );

        final Button applyButton = new Button( "filter-apply" );

        // Todo disable button on javascript-disabled clients only
        // something like:
        // applyButton.add( new SimpleAttributeModifier( "onload", "this.disabled=true;") );
        // meanwhile...
        applyButton.setEnabled( false );

        tree = new FilterTree( "filter-tree", copyFilter() ){
            private static final long serialVersionUID = 188064423994094109L;

            @Override
            public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                applyButton.setEnabled( true );
                saveButton.setEnabled( true );
                target.addComponent( applyButton );
            }

            @Override
            public void onExpandCollapse( AjaxRequestTarget target, Filter filter ) {
            }
        };
        tree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );

        Form form = new Form( "filter-form" ){
            private static final long serialVersionUID = 2542195753411167205L;

            @Override
            protected void onSubmit() {
                applyButton.setEnabled( false );
                Filter filter = tree.getFilter();
                onFilterApplied( filter );
                tree.setFilter( copyFilter() );
            }
        };

        form.add( tree );
        form.add( applyButton );
        form.add( saveButton );
        add( form );
    }

    private Filter copyFilter() {
        try {
            return getFilter().clone();
        } catch ( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Override this for specific page behaviors.
     * @param filter the filter that was actually applied
     */
    public void onFilterApplied( Filter filter ){}

    /**
     * Override this for specific page behaviors.
     * @param filter the filter that was should be saved
     */
    public void onFilterSave( Filter filter ){}

    public final Filter getFilter() {
        return (Filter) getDefaultModelObject();
    }

    public final void setFilter( Filter filter ) {
        setDefaultModelObject( filter );
    }
}
