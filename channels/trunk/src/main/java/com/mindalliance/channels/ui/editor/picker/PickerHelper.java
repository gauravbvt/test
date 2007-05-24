// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.picker;

import java.util.ArrayList;
import java.util.Collection;

import com.beanview.BeanView;
import com.mindalliance.channels.data.elements.resources.Role;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class PickerHelper {

    public PickerHelper() { }
    
    public Collection<Role> findRole(BeanView bean) {
        Collection<Role> results = new ArrayList<Role>();
        for (int inx = 0 ; inx < 100 ; inx++) {
            Role role = new Role();
            role.setName( "Role " + inx );
            results.add( role );
        }
        return results;
    }
}
