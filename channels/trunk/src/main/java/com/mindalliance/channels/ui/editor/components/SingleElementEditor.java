// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.EditorFactory;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class SingleElementEditor<T> extends Hbox implements PropertyComponent {
    private SystemService system;
    private User user;
    private Class<T> type;
    private Object edited;
    private Label label;
    private Button editButton;
    private Button createButton;
    private Button removeButton;
    private EditorFactory factory;
    
    public SingleElementEditor( Class<T> type, SystemService system, User user ) {
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
    
    private Box createButtons() {
        Hbox buttonBox = new Hbox();
        createButton = createChooseButton();
        buttonBox.appendChild( createButton );
        editButton = createEditButton();
        buttonBox.appendChild( editButton );
        removeButton = createRemoveButton();
        buttonBox.appendChild( removeButton );
        return buttonBox;
    }
    
    private Button createChooseButton() {
        Button addButton = new Button( "Choose" );
        addButton.setImage( "images/16x16/add2.png" );
        addButton.setTooltiptext( "Choose a "
                + type.getSimpleName() );
        return addButton;
    }
    private Button createEditButton() {
        Button editButton = new Button( "Edit" );
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
    
    private Button createRemoveButton() {
        Button removeButton = new Button( "Remove" );
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
            editButton.setDisabled( true );
        } else {
            labelVal = edited.toString();
            editButton.setDisabled( false );
        }
        label.setValue( labelVal );
    }
    
    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) {
        this.edited = arg0;
        refreshLabel();
    }

}
