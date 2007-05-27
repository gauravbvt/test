// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.acegisecurity.GrantedAuthority;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;

import com.mindalliance.channels.User;

/**
 * Summary of what happens when the user selects an item in a tab
 * in the accordion pane.
 *
 * @todo Make this a bonified command
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AccordionSelection {

    private String icon;
    private String label;
    private String tooltip;
    private String roles;
    private Component canvas;
    private ObjectEditor editor;
    private ObjectBrowser browser;

    /**
     * Default constructor.
     *
     * @param icon path to the icon to display in lists
     * @param label the label of the selection
     * @param tooltip the tooltip of the selection
     * @param roles authorized roles for this selection
     * @param canvas the associated canvas
     * @param editor the pane to display in the canvas when selected
     */
    public AccordionSelection(
            String icon, String label, String tooltip, String roles,
            Component canvas, ObjectEditor editor ) {

        if ( editor == null )
            throw new NullPointerException();

        this.icon = icon;
        this.label = label;
        this.tooltip = tooltip;
        this.roles = roles;
        this.canvas = canvas;
        this.editor = editor;
    }

    /**
     * Default constructor.
     *
     * @param icon path to the icon to display in lists
     * @param label the label of the selection
     * @param tooltip the tooltip of the selection
     * @param roles authorized roles for this selection
     * @param canvas the associated canvas
     * @param browser the pane to display in the canvas when selected
     */
    public AccordionSelection(
            String icon, String label, String tooltip, String roles,
            Component canvas, ObjectBrowser browser ) {

        if ( browser == null )
            throw new NullPointerException();

        this.icon = icon;
        this.label = label;
        this.tooltip = tooltip;
        this.roles = roles;
        this.canvas = canvas;
        this.browser = browser;
    }

    /**
     * Return the value of icon.
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * Return the value of label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Return the value of tooltip.
     */
    public String getTooltip() {
        return this.tooltip;
    }

    /**
     * Perform the action associated with this selection.
     */
    @SuppressWarnings( "unchecked" )
    public void select() {
        List children = new ArrayList( canvas.getChildren() );
        for ( Object child : children )
            canvas.removeChild( (Component) child );

        canvas.appendChild(
            (Component) ( getEditor() == null ? getBrowser() : getEditor() ) );
        canvas.invalidate();

        Session session = canvas.getRoot().getDesktop().getSession();
        session.setAttribute( DesktopRichlet.CURRENT_SELECTION, this );
    }

    /**
     * Get the authorized roles for this selection.
     */
    public String getRoles() {
        return this.roles;
    }

    /**
     * Test if a user is authorized to perform this selection.
     * @param user the user
     */
    public boolean isAuthorized( User user ) {
        StringTokenizer tokenizer = new StringTokenizer( getRoles(), ", " );
        while ( tokenizer.hasMoreTokens() ) {
            String role = tokenizer.nextToken();
            for ( GrantedAuthority a : user.getAuthorities() ) {
                if ( a.getAuthority().equals( role ) )
                    return true;
            }
        }
        return false;
    }

    /**
     * Get the canvas associated with this selection.
     */
    public Component getCanvas() {
        return this.canvas;
    }

    /**
     * Return the value of editor.
     */
    public ObjectEditor getEditor() {
        return this.editor;
    }

    /**
     * Return the value of browser.
     */
    public ObjectBrowser getBrowser() {
        return this.browser;
    }
}
