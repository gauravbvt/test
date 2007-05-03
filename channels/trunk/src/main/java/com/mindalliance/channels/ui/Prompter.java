// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * An annoying popup dialog to ask users to input a single string value...
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public final class Prompter extends Window {

    private boolean ok;
    private Textbox textbox;

    /**
     * Default constructor.
     *
     * @param title the title of the prompt dialog
     * @param prompt the text before the entry field.
     * @param defaultValue the default value in the entry field.
     * @throws InterruptedException if the dialog thread was interrupted
     */
    private Prompter( String title, String prompt, String defaultValue )
        throws InterruptedException {

        super( title, "normal", false );

        Button okButton = new Button( "Ok" );
        okButton.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                setVisible( false );
                setOk( true );
            }
        } );

        Button cancelButton = new Button( "Cancel" );
        cancelButton.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                setVisible( false );
            }
        } );

        Hbox buttons = new Hbox();
        buttons.appendChild( okButton );
        buttons.appendChild( cancelButton );
        buttons.setWidth( "100%" );

        textbox = new Textbox( defaultValue );

        Vbox vbox = new Vbox();
        vbox.appendChild( new Text( prompt ) );
        vbox.appendChild( textbox );
        vbox.appendChild( buttons );

        appendChild( vbox );
        setWidth( "50%" );
        setPage( (Page) Executions.getCurrent().getDesktop()
                          .getPages().iterator().next() );
        doModal();
    }

    /**
     * Ask the user for a value.
     *
     * @param title the title of the prompt dialog
     * @param prompt the text before the entry field.
     * @param defaultValue the default value in the entry field.
     * @return Input value, or null if user cancelled.
     * @throws InterruptedException if the dialog thread was interrupted
     */
    public static String prompt(
            String title, String prompt, String defaultValue )
        throws InterruptedException {

        Prompter p = new Prompter( title, prompt, defaultValue );
        return p.isOk()? p.getValue() : null ;
    }

    /**
     * Return true if the user closed the prompt be pressing Ok.
     */
    public boolean isOk() {
        return this.ok;
    }

    /**
     * Return the string value of the entry field.
     */
    public String getValue() {
        return this.textbox.getValue();
    }

    /**
     * Set the value of ok.
     * @param ok The new value of ok
     */
    public void setOk( boolean ok ) {
        this.ok = ok;
    }
}
