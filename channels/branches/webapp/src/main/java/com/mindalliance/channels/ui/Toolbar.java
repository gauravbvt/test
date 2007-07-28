// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.system.SystemService;
import com.mindalliance.channels.search.ui.SearchWindowManager;

/**
 * The main desktop's toolbar.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class Toolbar extends Hbox {

    /**
     * Default constructor.
     *
     * @param user the user of this toolbar
     * @param system the system behind the scene
     */
    public Toolbar( final User user, final SystemService system ) {
        super();

        setSclass( "channels_toolbar" );
        setHeight( "40px" );
        setWidth( "100%" );
        setValign( "middle" );

        Html html = new Html(
            "<img align=\"middle\" src=\"images/channels.png\"></img>"
                + "<a href=\"profile.jsp\">"
                + user.getName() + "</a>"
                + " <a href=\"logout.jsp\">(logout)</a>" );
        html.setWidth( "100%" );
        html.setSclass( "logo" );
        appendChild( html );
        appendChild( createIcons() );
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
            if ( spec[0].equals( "Search" ) )
                button.addEventListener(
                    "onClick", new SearchWindowManager() );

            iconbar.appendChild( button );
        }
        return iconbar;
    }
}
