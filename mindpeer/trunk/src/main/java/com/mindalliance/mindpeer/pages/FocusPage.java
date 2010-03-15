// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.model.Focus;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.model.User;
import com.mindalliance.mindpeer.pages.components.CommentsPanel;
import com.mindalliance.mindpeer.pages.components.ExpertsPanel;
import com.mindalliance.mindpeer.pages.components.TabPanel;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A focus-based page.
 */
public class FocusPage extends ItemPage<Focus> {

    private static final long serialVersionUID = 1259001648441840208L;

    /**
     * Constructor which receives wrapped query string parameters for a request.
     * @param parameters Wrapped query string parameters.
     */
    public FocusPage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * ...
     *
     * @param profile the given profile
     * @param name the given name
     * @return T
     */
    @Override
    protected Focus load( Profile profile, String name ) {
        // TODO use legitimate query?
        for ( Focus focus : profile.getUser().getFocusList() )
            if ( focus.getName().equals( name ) )
                return focus;
        return null;
    }

    /**
     * ...
     *
     * @param p the given p
     * @param name the given name
     */
    @Override
    protected void createItem( Profile p, String name ) {
        User u = p.getUser();
        u.add( new Focus( name ) );
        saveUser( u );
    }

    /**
     * ...
     * @return String
     */
    @Override
    protected String newItemName() {
        return "untitled";
    }

    /**
     * Return the ItemPage's tabs.
     * @return the value of tabs
     */
    @Override
    protected TabPanel.Tab[] getTabs() {
        return new TabPanel.Tab[] {
                new TabPanel.Tab( "Comments" ) {
                    private static final long serialVersionUID = 4287046677188347549L;

                    @Override
                    public Component getComponent( String id ) {
                        return new CommentsPanel( id );
                    }
                },
                new TabPanel.Tab( "Documents" ) {
                    private static final long serialVersionUID = 4287046677188347549L;

                    @Override
                    public Component getComponent( String id ) {
                        return new Label( id, "(List of commented documents)" );
                    }
                },
                new TabPanel.Tab( "Experts" ) {
                    private static final long serialVersionUID = 4287046677188347549L;

                    @Override
                    public Component getComponent( String id ) {
                        return new ExpertsPanel( id );
                    }
                },
        };
    }

    /**
     * Get the title of the right-hand list.
     * @return the list title
     */
    @Override
    public String getListTitle() {
        return "Focus List";
    }

    @Override
    public Focus getEmptyItem() {
        return new Focus( "Focii" );
    }

    @Override
    public void renameAndSave( String oldName, Focus object ) {
        User user = getUser();
        List<Focus> list = user.getFocusList();
        list.remove( object );
        list.add( object );
    }

    @Override
    public Iterator<IModel<Focus>> getIterator( IModel<Profile> profile ) {
        List<IModel<Focus>> result = new ArrayList<IModel<Focus>>();
        for ( Focus focus : getUser().getFocusList() )
            result.add( new Model<Focus>( focus ) );

        return result.iterator();
    }

    @Override
    public String getDefaultSection() {
        return "Comments";
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return getUser().getProfile().getName();
    }

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return 0;
    }

    //============================================
    /**
     * Manufactured focus aggregation.
     */
    private class AllFocus extends Focus {

        private static final long serialVersionUID = 7719183375380381261L;

        /**
         * Create a new AllFocus instance.
         */
        private AllFocus() {
            super( "all" );
            setUser( FocusPage.this.getUser() );
        }
    }
}
