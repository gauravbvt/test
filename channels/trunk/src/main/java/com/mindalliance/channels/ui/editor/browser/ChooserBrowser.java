// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.AbstractBrowser;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ChooserBrowser<T> extends AbstractBrowser<T> {

    public ChooserBrowser( Class<T> type, Class collectionType, SystemService system, User user ) {
        super( type, collectionType, system, user );
    }

    public ChooserBrowser( Class<T> type, SystemService system, User user ) {
        super( type, system, user );
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.ui.editor.AbstractBrowser#performAddAction()
     */
    @Override
    protected void performAddAction() {
        try {
            JavaBean result = getEditorFactory().popupEditor( (JavaBean) getModel().getObjectClass().newInstance());
            if (result != null) {
                getModel().add( result );
            }
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.ui.editor.AbstractBrowser#performEditAction()
     */
    @Override
    protected void performEditAction() {
        int index = getBrowser().getSelectedIndex();
        if ( index >= 0 ) {
            JavaBean result = getEditorFactory().popupEditor(  (JavaBean)getModel().getElementAt( index ) );
            if (result != null) {
                setObjects(getObjects());
            }
        }
        
        // TODO do whatever needs to be done for persistence
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.ui.editor.AbstractBrowser#performRemoveAction()
     */
    @Override
    protected void performRemoveAction() {
        int index = getBrowser().getSelectedIndex();
        if ( index >= 0 ) {
            getModel().remove( getModel().getElementAt( index ) );
        }
        
        // TODO update the global data model
    }

}
