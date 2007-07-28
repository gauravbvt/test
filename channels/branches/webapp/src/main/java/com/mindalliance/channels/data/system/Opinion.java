// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import com.mindalliance.channels.data.support.GUID;

/**
 * An opinion about an element's definition.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Opinion extends Statement {

    /**
     * Intent of an opinion
     * (approval, change recommended, should be removed).
     */
    enum Intent {
        /** Approval. */
        OK, CHANGE, REMOVE
    }

    private Intent intent;

    /**
     * Default constructor.
     */
    public Opinion() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Opinion( GUID guid ) {
        super( guid );
    }

    /**
     * Return the intent.
     */
    public Intent getIntent() {
        return intent;
    }

    /**
     * Set the intent.
     * @param intent the intent to set
     */
    public void setIntent( Intent intent ) {
        this.intent = intent;
    }
}
