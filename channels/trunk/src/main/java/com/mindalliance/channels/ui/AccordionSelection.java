// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.acegisecurity.GrantedAuthority;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;

import com.mindalliance.channels.User;

// TODO Make this a bonified action

/**
 * Summary of what happens when the user selects an item in a tab
 * in the accordion pane.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AccordionSelection {

    private String icon;
    private String label;
    private Component pane;
    private String tooltip;
    private String roles;
    private Component canvas;
    private Object object;

    /**
     * Default constructor.
     *
     * @param icon path to the icon to display in lists
     * @param label the label of the selection
     * @param tooltip the tooltip of the selection
     * @param roles authorized roles for this selection
     * @param pane the pane to display in the canvas when selected
     * @param canvas the associated canvas
     */
    public AccordionSelection(
            String icon, String label, String tooltip, String roles,
            Component pane, Component canvas ) {

        this.icon = icon;
        this.label = label;
        this.pane = pane;
        this.tooltip = tooltip;
        this.roles = roles;
        this.canvas = canvas;
    }

    /**
     * Default constructor.
     *
     * @param icon path to the icon to display in lists
     * @param label the label of the selection
     * @param tooltip the tooltip of the selection
     * @param roles authorized roles for this selection
     * @param pane the pane to display in the canvas when selected
     * @param canvas the associated canvas
     * @param object the model object behind this selection item
     */
    public AccordionSelection(
            String icon, String label, String tooltip, String roles,
            Component pane, Component canvas, Object object ) {

        this( icon, label, tooltip, roles, pane, canvas );
        this.object = object;
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
     * Return the value of pane.
     */
    public Component getPane() {
        return this.pane;
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

        canvas.appendChild( pane != null ? pane
                                         : new Text( "TBD: " + label ) );
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
     * Set the value of canvas.
     * @param canvas The new value of canvas
     */
    public void setCanvas( Component canvas ) {
        this.canvas = canvas;
    }

    /**
     * Return the value of the underlying object for this selection.
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * Set the value of object.
     * @param object The new value of object
     */
    public void setObject( Object object ) {
        this.object = object;
    }
}
