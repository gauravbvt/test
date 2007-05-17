// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.util.AbstractJavaBean;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class BeanEditor extends Window {
    
    private AbstractJavaBean editedBean;
    private User user;
    private System system;
    private Scenario scenario;
    
    public BeanEditor(System system, User user, Scenario scenario, AbstractJavaBean object) {
        super("Editor", "normal", false);
        this.system = system;
        this.user = user;
        this.editedBean = object;
        initializeEditor();
    }
    
    private void initializeEditor() {
        this.setWidth( "400px" );
        this.setHeight( "200px" );
        this.appendChild( new Label("TBD") );
        Button closeButton = new Button("Close");
        closeButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                setVisible( false );
            }
        });

        appendChild(closeButton);
        doOverlapped();
        
    }

    
    /**
     * Return the value of editedBean.
     */
    public AbstractJavaBean getEditedBean() {
        return editedBean;
    }

    
    /**
     * Set the value of editedBean.
     * @param editedBean The new value of editedBean
     */
    public void setEditedBean( AbstractJavaBean object ) {
        this.editedBean = object;
    }
}
