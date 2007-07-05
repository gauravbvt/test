// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementBrowser<T> extends AbstractBrowser<T> {

    public ElementBrowser( Class<T> type, SystemService system, User user ) {
        super( type, system, user );
    }

    public ElementBrowser( Class<T> type, Class<T> collectionType,
            SystemService system, User user ) {
        super( type, collectionType, system, user );
    }

    protected void performEditAction() {
        int index = getBrowser().getSelectedIndex();
        if ( index >= 0 ) {
            JavaBean result = getEditorFactory().popupEditor(  (JavaBean)getModel().getElementAt( index ) );
            if (result != null) {
                setObjects(getObjects());
            }
        }
    }

    protected void performRemoveAction() {
        int index = getBrowser().getSelectedIndex();
        
        if ( index >= 0 ) {
            getModel().remove( index );
        }
    }

    protected void performAddAction() {
        T result = getEditorFactory().popupChooser( getModel().getObjectClass() );
        if (result != null && !getModel().contains( result )) {
            getModel().add( result );
            
        }
    }

}
