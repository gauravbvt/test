// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class InterfaceChooser<T> extends Window {

    private Class<T> interfaceType;
    private ListModelArray model;
    private Listbox list;
    private Button okButton;
    private Button cancelButton;
    private boolean ok = false;
    
    public InterfaceChooser(Class<T> type, Class[] options) {
        super(type.getSimpleName(), "normal", false);
        list = new Listbox();
        list.setRows( 11 );
        list.setMold( "paging" );
        list.setPageSize( 10 );
        list.setWidth( "400px" );
        model = new ListModelArray(options);
        list.setModel( model );
        list.setItemRenderer( new ClassListitemRenderer() );
        appendChild(list);
        
        okButton = new Button("OK");
        okButton.setDisabled( true );
        okButton.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                
                ok = true;
                InterfaceChooser.this.setVisible(false);
            }         
        });
        
        cancelButton = new Button("Cancel");
        cancelButton.addEventListener( "onClick", new EventListener() { 
            public boolean isAsap() {
                return false;
            }
            public void onEvent( Event event ) {
                ok = false;
                InterfaceChooser.this.setVisible( false );
            }
        });
        list.addEventListener(Events.ON_SELECT, new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event arg0 ) {
                if (list.getSelectedIndex() >= 0) {
                    okButton.setDisabled( false );
                } else {
                    okButton.setDisabled( true );
                }
            }
        });
        Hbox box = new Hbox();
        box.appendChild(okButton);
        box.appendChild(cancelButton);
        appendChild(box);
        this.setWidth( "415px" );
    }
    
    public boolean isOk() {
        return ok;
    }
    
    public Class<T> getSelectedType() {
        return (Class<T>)model.getElementAt( list.getSelectedIndex() );
    }
    
    
    private class ClassListitemRenderer implements ListitemRenderer {

        /* (non-Javadoc)
         * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object)
         */
        public void render( Listitem li, Object obj ) throws Exception {
            Class c  = (Class)obj;
            new Listcell(c.getSimpleName()).setParent(li);
        }
        
    }
    
}
