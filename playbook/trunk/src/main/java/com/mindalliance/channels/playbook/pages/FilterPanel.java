package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.FilterTree;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;

/**
 * ...
 */
public class FilterPanel extends Panel {

    private Filter filter;

    public FilterPanel( String id, IModel model ) {
        super( id, model );
        setFilter( getTab().getFilter() );

        final Button saveButton = new Button( "filter-save" ) {
            public void onSubmit() {
                setEnabled(false);
                Tab newTab = new Tab();
                Ref newTabRef = newTab.persist();
                Filter f = getFilter().copy();
                newTab.setBase( new FilteredContainer( getTab().getBase(), f ) );
                List<Class<?>> c = f.getContainer().getAllowedClasses();
                if ( c.size() > 0 ) {
                    // TODO do something smarter here...
                    newTab.setName( ColumnProvider.toDisplay( c.get(0).getSimpleName() ) + "s" );
                }

                final PlaybookSession s = (PlaybookSession) Session.get();
                Ref userRef = s.getUser();
//                userRef.begin();
                User u = (User) userRef.deref();
                u.addTab( newTabRef );
                newTab.commit();
                userRef.commit();
            }
        };
        saveButton.setEnabled( false );

        final Button applyButton = new Button( "filter-apply" );

        // Todo disable button on javascript-disabled clients only
        // something like:
        // applyButton.add( new SimpleAttributeModifier( "onload", "this.disabled=true;") );
        // meanwhile...
        applyButton.setEnabled( false );

        final FilterTree tree = new FilterTree( "filter-tree", new DefaultTreeModel( getFilter() )){
            public void onCheckBoxUpdate( AjaxRequestTarget target, Filter filter ) {
                applyButton.setEnabled( true );
//                saveButton.setEnabled( true );
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
                final Ref tabRef = getTabRef();
                tabRef.begin();
                Tab tab = (Tab) tabRef.deref();
                tab.setFilter( getFilter() );
                tabRef.commit();
                onFilterApplied( getFilter() );
            }
        };


        form.add( tree );
        form.add( applyButton );
        form.add( saveButton );
        add( form );
    }

    public Ref getTabRef() {
        return (Ref) getModelObject();
    }

    public Tab getTab() {
        return (Tab) getTabRef().deref();
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
