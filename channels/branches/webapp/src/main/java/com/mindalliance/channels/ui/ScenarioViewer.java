// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.text.MessageFormat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.ui.editor.EditorFactory;

/**
 * The world-famous scenario viewer.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ScenarioViewer extends Window
    implements ObjectEditor {

    private static final int TOP_MARGIN = 42;
    private static final int BOTTOM_MARGIN = 35;
    private static final int PADDING = 20;
    private static final int TIMELINE_HEIGHT = 128;

    private EditorFactory editorFactory;
    private Scenario scenario;
    private ScenarioTimeline timeline;

    /**
     * Default constructor.
     *
     * @param height the available height
     * @param scenario the scenario
     * @param editorFactory the editor creator
     */
    public ScenarioViewer(
            int height, Scenario scenario, EditorFactory editorFactory ) {

        super();
        this.scenario = scenario;
        this.editorFactory = editorFactory;
        Page page = editorFactory.getPage();
        IconManager im = getIconManager( page );

        int kidHeight = height - TOP_MARGIN - PADDING - TIMELINE_HEIGHT;

        timeline = new ScenarioTimeline( TIMELINE_HEIGHT, im, page, scenario );
        Hbox bottom = new Hbox();
        bottom.appendChild( createWherePane( kidHeight, im, timeline ) );
        bottom.appendChild( createWhatPane( kidHeight, im, timeline ) );
        bottom.appendChild( createWhoPane( kidHeight, im, timeline ) );
        bottom.setWidth( "100%" );
        bottom.setWidths( "33%,33%" );
        bottom.setSclass( "bottom-pane" );

        Vbox contents = new Vbox();
        contents.appendChild( createDescription() );
        contents.appendChild( timeline );
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
     * @param im the icon manager
     * @param timeline the timeline
     */
    private Window createWhatPane(
            int height, IconManager im, ScenarioTimeline timeline ) {

        TreeGraphPane tgp = new TreeGraphPane(
                height, getScenario(), getEditorFactory() );
        tgp.setIconManager( im );
        tgp.setTimeline( this.timeline );
        timeline.addTimelineListener( tgp );

        Window window = new Window( "What", "normal", false );
        window.appendChild( tgp );
        window.setHeight( height + "px" );
        return window;
    }

    /**
     * Create a "who" bottom pane.
     * @param height the initial height
     * @param im the icon manager
     * @param timeline the timeline
     */
    private Window createWhoPane(
            int height, IconManager im, ScenarioTimeline timeline ) {

        TreeGraphPane tgp = new TreeGraphPane(
                height, getScenario(), getEditorFactory() );
        tgp.setIconManager( im );
        tgp.setTimeline( this.timeline );
//        timeline.addTimelineListener( tgp );

        Window window = new Window( "Who", "normal", false );
        window.appendChild( tgp );
        window.setHeight( height + "px" );
        return window;
    }

    /**
     * Create a dummy map centered on DK's home...
     * @param height the initial height
     * @param im the icon manager
     * @param timeline the timeline
     */
    private Window createWherePane(
            int height, IconManager im, ScenarioTimeline timeline ) {

        Div div = new Div();
        div.appendChild( new Image( "images/map.png" ) );
        div.setSclass( "google-map" );
        div.setHeight( ( height - BOTTOM_MARGIN ) + "px" );

        Hbox box = new Hbox();
        box.appendChild( div );

        Window window = new Window( "Where", "normal", false );
        window.appendChild( box );
        window.setHeight( height + "px" );
        return window;
    }

    /**
     * Create the description portion of the viewer.
     */
    private Textbox createDescription( ) {
        Textbox result = new Textbox( getScenario().getDescription() );
        result.setWidth( "100%" );
        result.setSclass( "description" );
        result.addEventListener( "onChange", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event event ) {
                // TODO Make a command out of this...
                InputEvent ie = (InputEvent) event;
                getScenario().setDescription( ie.getValue() );
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
     * Get a hold of the icon manager in the application context.
     * @param page the page
     */
    private IconManager getIconManager( Page page ) {
        Session zkSession = page.getDesktop().getSession();
        HttpSession httpSession = (HttpSession) zkSession.getNativeSession();
        ServletContext servletContext = httpSession.getServletContext();
        ApplicationContext appContext =
            (ApplicationContext) servletContext.getAttribute(
                "org.springframework.web.context.WebApplicationContext.ROOT" );

        return (IconManager) appContext.getBean( "iconManager" );
    }

    /**
     * Return the object edited by this editor.
     */
    public JavaBean getObject() {
        return getScenario();
    }

    /**
     * Return the value of editorFactory.
     */
    public EditorFactory getEditorFactory() {
        return this.editorFactory;
    }
}
