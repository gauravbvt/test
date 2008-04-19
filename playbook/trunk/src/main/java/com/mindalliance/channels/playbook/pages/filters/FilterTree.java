package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * ...
 */
public abstract class FilterTree extends Tree {

    public FilterTree( String id, TreeModel model ) {
        super( id, model );
    }

    public abstract void onCheckBoxUpdate( AjaxRequestTarget target, Filter filter );

    @Override
    protected String renderNode( TreeNode node ) {
        Filter f = (Filter) node;
        return f.getText();
    }

    @Override
    protected void populateTreeItem( final WebMarkupContainer item, int level ) {
        super.populateTreeItem( item, level );
        final AjaxCheckBox checkBox = new AjaxCheckBox( "filter-check",
            new PropertyModel( item.getModelObject(), "selected" ) ){

            protected void onUpdate( AjaxRequestTarget target ) {
                onCheckBoxUpdate( target, (Filter) item.getModelObject() );
            }
        };
        checkBox.setOutputMarkupId( true );

        item.add( checkBox );
        Filter f = (Filter) item.getModelObject();
        if ( f.isExpanded() )
        getTreeState().expandNode( f );
    }

    protected MarkupContainer newNodeLink( MarkupContainer parent, String id, TreeNode node ) {
        final WebMarkupContainer markupContainer = new WebMarkupContainer( id );
        markupContainer.setRenderBodyOnly( true );
        return markupContainer;
    }

    protected void onJunctionLinkClicked( AjaxRequestTarget target, TreeNode node ) {
        super.onJunctionLinkClicked( target, node );
        Filter f = (Filter) node;
        f.setExpanded( getTreeState().isNodeExpanded( node ) );
    }
}
