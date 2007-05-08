// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.text.MessageFormat;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.model.Scenario;

/**
 * The world-famous scenario viewer.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ScenarioViewer extends Window {

    private static final int TOP_MARGIN = 42;
    private static final int PADDING = 20;
    private static final int TIMELINE_HEIGHT = 128;

    private System system;
    private Scenario scenario;
    private User user;

    /**
     * Default constructor.
     *
     * @param height the available height
     * @param page the page
     * @param system the system
     * @param scenario the scenario
     * @param user the user
     */
    public ScenarioViewer(
            int height, Page page,
            System system, Scenario scenario, User user ) {

        super();
        this.system = system;
        this.scenario = scenario;
        this.user = user;

        int kidHeight = height - TOP_MARGIN - PADDING - TIMELINE_HEIGHT;

        Hbox bottom = new Hbox();
        bottom.appendChild( createWherePane( kidHeight ) );
        bottom.appendChild( createWhatPane( kidHeight ) );
        bottom.appendChild( createWhoPane( kidHeight ) );
        bottom.setWidth( "100%" );
        bottom.setWidths( "33%,33%" );
        bottom.setSclass( "bottom-pane" );

        Vbox contents = new Vbox();
        contents.appendChild( createDescription() );
        contents.appendChild(
                new ScenarioTimeline( TIMELINE_HEIGHT, page, scenario ) );
        contents.appendChild( bottom );
        contents.setWidth( "100%" );
        contents.setHeights( "50%,50%" );
        contents.setSpacing( "5px" );

        this.appendChild( contents );
        this.setTitle(
            MessageFormat.format( "Scenario: {0}", scenario.getName() ) );
        this.setBorder( "normal" );
        this.setHeight( height + "px" );
    }

    /**
     * Create a "what" bottom pane.
     * @param height the initial height
     */
    private Window createWhatPane( int height ) {

        Window window = new Window( "What", "normal", false );
        TreeGraphPane tgp = new TreeGraphPane(
                height, getSystem(), getScenario(), getUser() );
        window.appendChild( tgp );
        window.setHeight( height + "px" );
        return window;
    }

    /**
     * Create a "who" bottom pane.
     * @param height the initial height
     */
    private Window createWhoPane( int height ) {

        Window window = new Window( "Who", "normal", false );
        TreeGraphPane tgp = new TreeGraphPane(
                height, getSystem(), getScenario(), getUser() );
        window.appendChild( tgp );
        window.setHeight( height + "px" );
        return window;
    }

    /**
     * Create a dummy map centered on DK's home...
     * @param height the initial height
     */
    private Window createWherePane( int height ) {

        Div div = new Div();
        div.appendChild( new Image( "images/map.png" ) );
        div.setSclass( "google-map" );

        Window window = new Window( "Where", "normal", false );
        window.appendChild( div );
        window.setHeight( height + "px" );
        return window;
    }

    /**
     * Create the description portion of the viewer.
     */
    private Textbox createDescription( ) {
        Textbox result = new Textbox( getScenario().getAbout() );
        result.setWidth( "100%" );
        result.setSclass( "description" );
        result.addEventListener( "onChange", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event event ) {
                // TODO Make a command out of this...
                InputEvent ie = (InputEvent) event;
                getScenario().setAbout( ie.getValue() );
            }
        } );
        return result;
    }

    /**
     * Return the value of scenario.
     */
    public final Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Return the value of system.
     */
    public final System getSystem() {
        return this.system;
    }

    /**
     * Return the value of user.
     */
    public final User getUser() {
        return this.user;
    }
}
