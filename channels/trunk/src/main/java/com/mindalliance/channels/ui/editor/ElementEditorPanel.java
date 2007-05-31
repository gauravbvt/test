// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.beanview.BeanViewGroup;
import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.ObjectEditor;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementEditorPanel<T extends JavaBean> extends Window implements
        ObjectEditor {

    private BeanViewGroup<T> group;
    private Tabbox tabbox;
    private Tabs tabs;
    private Tabpanels tabpanels;
    private Tabpanel mainpanel;
    private T edited;
    private Class type;
    private List<String> exclude = new ArrayList<String>();
    private SystemService system;
    private User user;
    private boolean dialog = false;
    private Button cancelButton;
    private boolean ok;

    public ElementEditorPanel( T edited, SystemService system, User user ) {
        super(edited.getClass().getSimpleName() + " Editor", "normal", false);
        this.edited = edited;
        this.type = edited.getClass();
        this.system = system;
        this.user = user;

        exclude.add( "class" );
        exclude.add( "incident" );
        group = new BeanViewGroup<T>();

        tabbox = new Tabbox();
        tabs = new Tabs();
        tabpanels = new Tabpanels();
        tabs.appendChild( new Tab( type.getSimpleName() ) );
        mainpanel = new Tabpanel();
        tabpanels.appendChild( mainpanel );

        initializeGroups();

        tabbox.appendChild( tabs );
        tabbox.appendChild( tabpanels );
        group.setDataObject( edited );
        appendChild( tabbox );
        tabbox.setSelectedPanel( mainpanel );
        appendChild( initializeButtons() );
        this.setWidth( "50%" );
    }

    private Box initializeButtons() {
        Hbox buttons = new Hbox();
        Button saveButton = new Button( "Save" );
        saveButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                group.updateObjectFromPanels();
                setOk( true );
                if ( isDialog() ) {
                    setVisible( false );
                }
            }

        } );
        buttons.appendChild( saveButton );

        cancelButton = new Button( "Cancel" );
        cancelButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                setOk( false );
                if ( isDialog() ) {
                    setVisible( false );
                }
            }
        } );
        cancelButton.setVisible( dialog );
        buttons.appendChild( cancelButton );
        return buttons;
    }

    private void initializeGroups() {
        createAbstractElement();
        mainpanel.appendChild( createExclusionGroup() );
    }

    private void createAbstractElement() {
        if ( AbstractElement.class.isAssignableFrom( type ) ) {
            ElementBeanViewPanel<T> panel = createGroup( new String[] { "name",
                "description", "inferred" } );
            mainpanel.appendChild( panel );

            Tab tab = new Tab( "Types" );
            tabs.appendChild( tab );
            Tabpanel tabpanel = new Tabpanel();
            tabpanel.appendChild( createGroup( new String[] { "typeSet" } ) );
            tabpanels.appendChild( tabpanel );
            tab = new Tab( "Assertions" );
            tabs.appendChild( tab );
            tabpanel = new Tabpanel();
            tabpanel.appendChild( createGroup( new String[] { "assertions" } ) );
            tabpanels.appendChild( tabpanel );
            tab = new Tab( "Issues" );
            tabs.appendChild( tab );
            tabpanel = new Tabpanel();
            tabpanel.appendChild( createGroup( new String[] { "issues" } ) );

            tabpanels.appendChild( tabpanel );
        }
    }

    private ElementBeanViewPanel<T> createGroup( String[] members ) {
        ElementBeanViewPanel<T> panel = new ElementBeanViewPanel<T>( type,
                system, user );
        panel.setSubView( members, false, false );
        group.addBeanView( panel );
        for ( String s : members ) {
            exclude.add( s );
        }
        return panel;
    }

    private ElementBeanViewPanel<T> createExclusionGroup() {
        ElementBeanViewPanel<T> panel = new ElementBeanViewPanel<T>( type,
                system, user );
        panel.setExcludeProperties( exclude.toArray( new String[0] ) );
        group.addBeanView( panel );
        return panel;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectEditor#getObject()
     */
    public JavaBean getObject() {
        return edited;
    }

    /**
     * Return the value of dialog.
     */
    public boolean isDialog() {
        return dialog;
    }

    /**
     * Set the value of dialog.
     * 
     * @param dialog The new value of dialog
     */
    public void setDialog( boolean dialog ) {
        this.dialog = dialog;
        cancelButton.setVisible( dialog );
    }

    /**
     * Return the value of ok.
     */
    public boolean isOk() {
        return this.ok;
    }

    /**
     * Set the value of ok.
     * 
     * @param ok The new value of ok
     */
    public void setOk( boolean ok ) {
        this.ok = ok;
    }

}
