package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.pages.filters.FilterTree;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.swing.tree.DefaultTreeModel;

/**
 * ...
 */
public class FilterPanel extends Panel {

    public FilterPanel( String id, IModel model ) {
        super( id, model );

        final FilterTree tree = new FilterTree( "filter-tree", new DefaultTreeModel( getRawData().getFilter() ) );
//        tree.getTreeState().setAllowSelectMultiple( true );
        tree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );
        add( tree );
    }

    public Tab getRawData() {
        return (Tab) ((Ref) getModelObject() ).deref();
    }
}
