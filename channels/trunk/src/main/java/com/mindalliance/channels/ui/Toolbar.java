// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zhtml.Text;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;

/**
 * The main desktop's toolbar.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class Toolbar extends Hbox {

    private User user;
    private System system;

    /**
     * Default constructor.
     *
     * @param user the user of this toolbar
     * @param system the system behind the scene
     */
    public Toolbar( User user, System system ) {
        super();
        this.user = user;
        this.system = system;

        setSclass( "channels_toolbar" );
        setHeight( "60px" );
        setWidth( "100%" );
        setValign( "middle" );

        appendChild(
            new Html( "<a class=\"logo\" href=\"profile.jsp\">"
                + user.getName() + "</a>" ) );

        appendChild( new Text( " Acting as " ) );
        appendChild( createRoleSelector() );
        appendChild( new Html( "<a href=\"logout.jsp\">Logout</a>" ) );
        appendChild( createIcons() );
    }

    /**
     * Create the role selection list box for the current user.
     */
    private Listbox createRoleSelector() {
        Listbox roles = new Listbox();
        roles.setWidth( null );
        for ( String role : new String[]{
            "Employee", "Fire Warden", "Group Manager" } )
                roles.appendItem( role, role );
        return roles;
    }

    /**
     * Create the icon buttons.
     */
    private Hbox createIcons() {
        Hbox iconbar = new Hbox();
        iconbar.setSpacing( "0px" );

        String[][] icons = new String[][]{
            { "Cut", "/images/16x16/cut.png" },
            { "Copy", "/images/16x16/copy.png" },
            { "Paste", "/images/16x16/paste.png" },
            { "Undo", "/images/16x16/undo.png" },
            { "Redo", "/images/16x16/redo.png" },
            { "Agree", "/images/16x16/nav_up_green.png" },
            { "Disagree", "/images/16x16/nav_down_red.png" },
            { "Chat", "/images/16x16/messages.png" },
            { "Search", "/images/16x16/find.png" },
            { "Todos", "/images/16x16/note_pinned.png" },
            { "Help", "/images/16x16/help.png" },
        };
        for ( String[] spec : icons ) {
            Button button = new Button( spec[0], spec[1] );
            button.setOrient( "vertical" );
            iconbar.appendChild( button );
        }
        return iconbar;
    }

    /**
     * Return the value of system.
     */
    public System getSystem() {
        return this.system;
    }

    /**
     * Return the value of user.
     */
    public User getUser() {
        return this.user;
    }

}
