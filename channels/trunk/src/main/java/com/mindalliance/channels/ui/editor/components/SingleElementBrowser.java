// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.EditorFactory;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class SingleElementBrowser<T> extends Hbox implements PropertyComponent {
    private SystemService system;
    private User user;
    private Class<T> type;
    private Object edited;
    private Label label;
    private Toolbarbutton editButton;
    private Toolbarbutton createButton;
    private Toolbarbutton removeButton;
    private EditorFactory factory;
    
    public SingleElementBrowser( Class<T> type, SystemService system, User user ) {
        this.system = system;
        this.user = user;
        this.type = type;
        init();
    }
    
    private void init() {
        label = new Label();
        appendChild(label);
        appendChild(createButtons());
    }
    
    private Toolbar createButtons() {
        Toolbar buttonBox = new Toolbar();
        createButton = createChooseButton();
        buttonBox.appendChild( createButton );
        editButton = createEditButton();
        buttonBox.appendChild( editButton );
        removeButton = createRemoveButton();
        buttonBox.appendChild( removeButton );
        return buttonBox;
    }
    
    private Toolbarbutton createChooseButton() {
        Toolbarbutton addButton = new Toolbarbutton( "Choose" );
        addButton.setImage( "images/16x16/add2.png" );
        addButton.setTooltiptext( "Choose a "
                + type.getSimpleName() );        
        addButton.addEventListener( "onClick", new EventListener() {

                    public boolean isAsap() {
                        return false;
                    }

                    public void onEvent( Event arg0 ) {
                        T result = getEditorFactory().popupChooser(  type );
                        if (result != null) {
                            edited = result;
                            refreshLabel();
                        }
                    }

                } );
        return addButton;
    }
    private Toolbarbutton createEditButton() {
        Toolbarbutton editButton = new Toolbarbutton( "Edit" );
        editButton.setImage( "images/16x16/preferences.png" );
        editButton.setTooltiptext( "Edit the selected " + type.getSimpleName() );
        editButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                JavaBean result = getEditorFactory().popupEditor(  (JavaBean)edited );
                if (result != null) {
                    refreshLabel();
                }
            }

        } );
        return editButton;
    }
    
    private Toolbarbutton createRemoveButton() {
        Toolbarbutton removeButton = new Toolbarbutton( "Remove" );
        removeButton.setImage( "images/16x16/delete2.png" );
        removeButton.setTooltiptext( "Remove the selected "
                + type.getSimpleName() );

        removeButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                setValue(null);
            }

        } );
        return removeButton;
    }
    
    private EditorFactory getEditorFactory() {
        if (factory == null) {
            factory = new EditorFactory(getPage(), system, user);
        }
        return factory;
    }
    
    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return edited;
    }

    private void refreshLabel() {
        String labelVal;
        if (edited == null) {
            labelVal = "<None Selected>";
            editButton.setVisible( true );
        } else {
            labelVal = edited.toString();
            editButton.setVisible( false );
        }
        label.setValue( labelVal );
    }
    
    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object edited ) {
        this.edited = edited;
        refreshLabel();
    }

}
