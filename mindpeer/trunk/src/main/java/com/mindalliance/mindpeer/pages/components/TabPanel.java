// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages.components;

import com.mindalliance.mindpeer.model.NamedModelObject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A tab panel with a visible selected portion and links to others.
 */
public class TabPanel<T extends NamedModelObject> extends Panel {

    private static final long serialVersionUID = 8181887199364414575L;

    private IModel<String> selectionModel;

    /**
     * Create a new TabPanel instance.
     *
     * @param id the given id
     * @param model the given model
     * @param selectionModel the given selectionModel
     * @param tabs the given tabs
     */
    public TabPanel( String id, IModel<T> model, IModel<String> selectionModel,
                     Tab... tabs ) {
        super( id, new CompoundPropertyModel<T>( model ) );

        this.selectionModel = selectionModel;
        final Tab[] actualTabs = model.getObject().getId() == null ? new Tab[0] : tabs;

        add(
                new Label( "name" ),
                new Label( "status", "an appropriate message" ),

                new ListView<Tab>( "tabs", Arrays.asList( actualTabs ) ) {
                    private static final long serialVersionUID = -5972303318729129387L;

                    @Override
                    protected void populateItem( ListItem<Tab> item ) {
                        Tab tab = item.getModelObject();

                        String tabLabel = tab.getLabel();
                        WebMarkupContainer container;
                        if ( tabLabel.equals( getSelection() ) ) {
                            container = new WebMarkupContainer( "tab-link" );
                            container.setRenderBodyOnly( true );
                            item.add( new AttributeModifier( "class", true,
                                                             new Model<String>( "selected" ) ) );

                        } else {
                            PageParameters parms = new PageParameters();
                            parms.put( "name", getName() );
                            parms.put( "section", tabLabel );
                            container = new BookmarkablePageLink<T>(
                                    "tab-link", getPage().getClass(), parms );
                        }

                        item.add( container.add( new Label( "tab-label", tabLabel ) ) );
                    }
                },

                getSelectedComponent( actualTabs )
        );
    }

    @SuppressWarnings( { "unchecked" } )
    private String getName() {
        T namedObject = (T) getDefaultModelObject();
        return namedObject == null ? "Unnamed" : namedObject.getName();
    }

    private Component getSelectedComponent( Tab[] tabs ) {
        for ( Tab tab : tabs ) {
            if ( tab.getLabel().equals( getSelection() ) )
                return tab.getComponent( "contents" );
        }

        return getDefaultContents();
    }

    private Component getDefaultContents() {
        return new Label( "contents", "Use the list to the right to add new items." );
    }

    /**
     * Return the TabPanel's selection.
     * @return the selection
     */
    public String getSelection() {
        return selectionModel.getObject();
    }

    //===================================================
    /**
     * Tab specification. Either a component (the selected tab) or a page link.
     */
    public abstract static class Tab implements Serializable {
        private static final long serialVersionUID = 6144831801940976274L;

        private String label;

        /**
         * Create a new tab instance.
         *
         * @param label the given label
         */
        protected Tab( String label ) {
            this.label = label;
        }

        /**
         * Create the selected tab's component.
         * @param id the wicked id
         * @return the new component
         */
        public abstract Component getComponent( String id );

        /**
         * Return the tab's label.
         * @return the value of label
         */
        public String getLabel() {
            return label;
        }
    }
}
