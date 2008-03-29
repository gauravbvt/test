package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.ParentFilter;
import com.mindalliance.channels.playbook.pages.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class FilterPanel extends Panel {

    private IDataProvider rawData;
    private Filter filter;

    public FilterPanel( String id, Filter filter, IDataProvider rawData ) {
        super( id );
        this.filter = filter;
        this.rawData = rawData;
        final Form form = new Form( "filter-form" );
        add( form );

        final Tree tree = new Tree( "filter-tree", new DefaultTreeModel( filter ) ){
            protected String renderNode( TreeNode treeNode ) {
                Filter f = (Filter) treeNode;
                return f.isExpanded()? f.getExpandedText() : f.getCollapsedText();
            }

            /**
             * Callback function called after user clicked on an junction link. The node has already been expanded/collapsed
             * (depending on previous status).
             *
             * @param target Request target - may be null on non-ajax call
             * @param node   Node for which this callback is relevant
             */
            protected void onJunctionLinkClicked( AjaxRequestTarget target, TreeNode node ) {
                super.onJunctionLinkClicked( target, node );
            }

            /**
             * This callback method is called after user has selected / deselected the given node.
             *
             * @param target Request target - may be null on non-ajax call
             * @param node   Node for which this this callback is fired.
             */
            protected void onNodeLinkClicked( AjaxRequestTarget target, TreeNode node ) {
                super.onNodeLinkClicked( target, node );
            }

            protected void populateTreeItem( WebMarkupContainer webMarkupContainer, int i ) {
                super.populateTreeItem( webMarkupContainer, i );
        }
        };
        tree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );
        form.add( tree );
    }


    protected void addOrgs( Filter parent, ColumnProvider cp, IDataProvider data ) {
        if ( !cp.includes( "parent" ) )
            parent.add( new ParentFilter() );
        else { // do it the hard way
            boolean nullParents = false;
            Set<Organization> parents = new TreeSet<Organization>(
                    new Comparator<Organization>(){
                        public int compare( Organization o1, Organization o2 ) {
                            return o1.getName().compareTo( o2.getName() );
                        }
                    } );
            for ( Iterator i=data.iterator(0,data.size()) ; i.hasNext() ; ) {
                Ref r = (Ref) i.next();
                Object obj = r.deref();
                if ( obj instanceof Organization ) {
                    Organization org = (Organization) obj;
                    if ( org.getParent() == null )
                        nullParents = true ;
                    else
                        parents.add( org );
                }
            }
        }
    }

    protected void addPersons( Filter parent, ColumnProvider cp, IDataProvider data ) {
    }

    public IDataProvider getFilteredData() {
        return new IDataProvider(){

            public void detach() {
            }

            public Iterator iterator( int first, int count ) {
                return rawData.iterator( first, count );
            }

            public IModel model( Object object ) {
                return rawData.model( object );
            }

            public int size() {
                return rawData.size();
            }
        };
    }

    public IDataProvider getRawData() {
        return rawData;
    }

    public void setRawData( IDataProvider rawData ) {
        this.rawData = rawData;
    }
}
