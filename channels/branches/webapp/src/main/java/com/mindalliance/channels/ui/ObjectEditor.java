// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zk.ui.Component;

import com.mindalliance.channels.JavaBean;

/**
 * A generic object editor.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface ObjectEditor extends Component {

    /**
     * Return the object being edited.
     */
    JavaBean getObject();
}
