// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A generic MindPeer page with the top navigation bar and search form.
 * This page requires a logged in user.
 */
public abstract class AbstractUserPage extends WebPage {

    private static final long serialVersionUID = 4814301195901511507L;

    @SpringBean
    private UserDao userDao;

    private String query;

    private Long userId;

    /**
     * Create a new MindPeerPage instance.
     *
     * @param parameters the given parameters
     */
    protected AbstractUserPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    /**
     * Return the AbstractUserPage's user.
     * @return the value of user
     */
    public User getUser() {
        return (User) getDefaultModelObject();
    }

    /**
     * Add default components to the page.
     */
    @Secured( "ROLE_USER" )
    public void init() {
        userId = userDao.currentUserId();
        setDefaultModel( new CompoundPropertyModel<User>(
            new LoadableDetachableModel<User>() {
                private static final long serialVersionUID = -1942706621756265291L;

                @Override
                protected User load() {
                    return userDao.load( userId );
                }
            } ) );

        add( CSSPackageResource.getHeaderContribution( "static/mindpeer.css" ) );

        add(
            new Label( "title", new PropertyModel<Object>( this, "fullTitle" ) ),
            new BookmarkablePageLink<Void>( "public-home", PublicHomePage.class  ),
            new ExternalLink( "logout", "/j_spring_security_logout" ).setContextRelative( true ),
            new Form<Void>( "search" ) {
                private static final long serialVersionUID = 8039348683184207000L;

                @Override
                protected void onSubmit() {
                    PageParameters parameters = new PageParameters();
                    parameters.add( "q", getQuery() );
                    setResponsePage( SearchResultPage.class, parameters  );
                }
            }.add( new TextField<String>( "q", new PropertyModel<String>( this, "query" ) ) ),

            new ListView<TopMenuItem>( "menu", Arrays.asList(
                    new TopMenuItem( "Focus", FocusPage.class ),
                    new TopMenuItem( "Products", ProductPage.class ),
                    new TopMenuItem( "Account", AccountPage.class )
                ) ) {

                private static final long serialVersionUID = 4646212219746534511L;

                @Override
                    protected void populateItem( ListItem<TopMenuItem> item ) {
                        TopMenuItem mi = item.getModelObject();

                        WebMarkupContainer container;
                        if ( item.getIndex() == getSelectedTopItem() ) {
                            item.add( new AttributeModifier(
                                    "class", true, new Model<String>( "selected" ) ) );
                            container = new WebMarkupContainer( "link" );
                            container.setRenderBodyOnly( true );

                        } else {
                            container = new BookmarkablePageLink<Void>( "link", mi.getLink() );
                        }

                        item.add( container );
                        container.add(
                            new Label( "text", mi.getLabel() ).setRenderBodyOnly( true ) );
                    }
                } );
    }

    /**
     * Save a user.
     *
     * @param user the given user
     * @return the saved version of the user
     */
    @Transactional
    protected User saveUser( User user ) {
        return userDao.save( user );
    }


    /**
     * Return the page's full title.
     * @return the page title, prefix by "MindPeer - "
     */
    public String getFullTitle() {
        return "MindPeer - " + getTitle();
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    public abstract String getTitle();

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    protected abstract int getSelectedTopItem();

    /**
     * Return the page's query field valur.
     * @return the value of query field
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query of this page.
     * @param query the new query value.
     *
     */
    public void setQuery( String query ) {
        this.query = query;
    }

    /**
     * Return the userDao.
     * @return the value of userDao
     */
    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * Return the MindPeerPage's user.
     * @return the value of user
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the userId of this AbstractUserPage.
     * @param userId the new userId value.
     *
     */
    protected void setUserId( Long userId ) {
        this.userId = userId;
    }

    /**
     * Return the username of this page.
     * @return the value of the user's username
     */
    public String getUsername() {
        return getUser().getUsername();
    }

    /**
     * Return the url of the user's picture.
     * @return the picture's url
     */
    public String getPictureUrl() {
        // TODO get picture link from mount
        return getUsername() + "/avatar.png";

//        WicketApplication app = (WicketApplication) getApplication();
//        IRequestCodingStrategy strategy = app.getRequestCycleProcessor().getRequestCodingStrategy();
//
//        PageParameters parms = new PageParameters();
//        parms.put( "0", getUserId().getUsername() );
//        parms.put( "1", "avatar.png" );
//        CharSequence path = strategy.pathForTarget(
//                new BookmarkablePageRequestTarget( UserDispatchPage.class, parms ) );
//        return path.toString();

    }

    //=========================================
    /**
     * Convenience class for associating a label to a page.
     */
    private static class TopMenuItem implements Serializable {
        private static final long serialVersionUID = -5796840246767580300L;

        private Class<? extends Page> link;
        private String label;

        /**
         * Create a new TopMenuItem instance.
         *
         * @param label the given label
         * @param link the given link
         */
        private TopMenuItem( String label, Class<? extends Page> link ) {
            this.label = label;
            this.link = link;
        }

        /**
         * Return the TopMenuItem's label.
         * @return the value of label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Return the TopMenuItem's link.
         * @return the value of link
         */
        public Class<? extends Page> getLink() {
            return link;
        }
    }
}
