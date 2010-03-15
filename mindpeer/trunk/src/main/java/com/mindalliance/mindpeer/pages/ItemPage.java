// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.model.NamedModelObject;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.pages.components.ItemNameEditor;
import com.mindalliance.mindpeer.pages.components.ListItem;
import com.mindalliance.mindpeer.pages.components.TabPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;
import java.util.Iterator;

/**
 * Common code for pages with an item selector on the right.
 * @see ProductPage
 * @see FocusPage
 * @param <T> the named model object displayed in the page.
 */
public abstract class ItemPage<T extends NamedModelObject> extends AbstractUserPage {

    private static final long serialVersionUID = -4450869921119377818L;

    private String itemName;

    private String section;

    private CompoundPropertyModel<T> itemModel;

    private IModel<Profile> profileModel;

    /**
     * Create a new ItemPage instance.
     *
     * @param parameters the given parameters
     */
    //================================================
    protected ItemPage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * ...
     *
     * @param profile the given profile
     * @param name the given name
     * @return T
     */
    protected abstract T load( Profile profile, String name );

    /**
     * ...
     *
     * @param p the given p
     * @param name the given name
     */
    protected abstract void createItem( Profile p, String name );

    /**
     * ...
     * @return String
     */
    protected abstract String newItemName();

    /**
     * Return the ItemPage's tabs.
     * @return the value of tabs
     */
    protected abstract TabPanel.Tab[] getTabs();

    /**
     * Get the title of the right-hand list.
     * @return the list title
     */
    public abstract String getListTitle();

    /**
     * Add default components to the page.
     */
    @Override
    @Secured( "ROLE_USER" )
    @Transactional
    public void init() {
        super.init();

        PageParameters parameters = getPageParameters();
        itemName = parameters.getString( "name" );
        section = parameters.getString( "section" );

        // TODO Add RSS support for product pages
        if ( parameters.getString( "type" ) != null )
            throw new AbortWithWebErrorCodeException( SC_NOT_IMPLEMENTED );

        profileModel = new PropertyModel<Profile>( getDefaultModel(), "profile" );

        itemModel = new CompoundPropertyModel<T>(
                new LoadableDetachableModel<T>() {
                    private static final long serialVersionUID = 6119820082049827865L;

                    @Override
                    protected T load() {
                        return itemName == null ?
                                 getEmptyItem()
                               : ItemPage.this.load( profileModel.getObject(), itemName );
                    }
                } );

        if ( itemModel.getObject() == null )
            throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );

        add(
            new ContextImage( "pic", getPictureUrl() ),
            new BookmarkablePageLink<Void>( "profile-link", EditProfilePage.class ),
            new Label( "profile.name" ),
            new Label( "profile.subscriberCount" ),
            new Label( "profile.productCount" ).setRenderBodyOnly( true ),
            new Label( "list-title", getListTitle() ),

            new TabPanel<T>( "tabs", itemModel, new Model<String>( section ), getTabs() ),
            createNewLink(),
            createList()
        );
    }

    public abstract T getEmptyItem();

    /**
     * Create the right-side list.
     * @return a Component
     */
    private Component createList() {
        return new RefreshingView<T>( "list", getProfileModel() ) {
            private static final long serialVersionUID = -5144249840090643156L;

            @SuppressWarnings( { "unchecked" } )
            @Override
            protected Iterator<IModel<T>> getItemModels() {
                return getIterator( (IModel<Profile>) getDefaultModel() );
            }

            @Override
            protected void populateItem( Item<T> item ) {
                IModel<T> model = item.getModel();

                boolean selected = model.getObject().getName().equals( itemName );
                item.add( selected ?
                      new ItemNameEditor<T>( "item", model ) {
                          private static final long serialVersionUID = 9169566455020864736L;

                          @Override
                          @Transactional
                          public void save( String oldName, T o ) {
                              renameAndSave( oldName, o );
                              redirect( o.getName() );
                          }
                      }
                    : new ListItem( "item", model, ItemPage.this.getClass() )
                            .setRenderBodyOnly( true ) );

                if ( selected )
                    item.add( new AttributeModifier( "class", true,
                                                     new Model<String>( "selected" ) ) );
            }
        };
    }

    public abstract void renameAndSave( String oldName, T object );

    public abstract Iterator<IModel<T>> getIterator( IModel<Profile> profile );

    /**
     * ...
     * @return Link<Profile>
     */
    private Link<Profile> createNewLink() {
        return new Link<Profile>( "newItem", profileModel ) {
            private static final long serialVersionUID = 566441544198622000L;

            @Override
            @Transactional
            public void onClick() {
                String root = newItemName();
                int suffixNumber = 1;
                String name = root;

                Profile p = getModelObject();
                while ( load( p, name ) != null )
                    name = root + Integer.toString( ++suffixNumber );

                createItem( p, name );
                redirect( name );
            }
        };
    }

    /**
     * Return the page's item model.
     * @return the item model
     */
    public CompoundPropertyModel<T> getItemModel() {
        return itemModel;
    }

    private void redirect( String name ) {
        PageParameters parms = new PageParameters();
        if ( section == null )
            section = getDefaultSection();
        parms.put( "section", section );
        parms.put( "name", name );
        setRedirect( true );
        setResponsePage( getClass(), parms );
    }

    public abstract String getDefaultSection();

    /**
     * Return the profile model.
     * @return the value of profile model
     */
    public IModel<Profile> getProfileModel() {
        return profileModel;
    }

    /**
     * Disconnect extra models.
     */
    @Override
    public void detachModels() {
        super.detachModels();
        if ( itemModel != null )
            itemModel.detach();
        if ( profileModel != null )
            profileModel.detach();
    }
}
