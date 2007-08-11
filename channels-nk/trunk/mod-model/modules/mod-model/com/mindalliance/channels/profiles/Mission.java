// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import com.mindalliance.channels.definitions.TypedObject;
import com.mindalliance.channels.definitions.Category.Taxonomy;

/**
 * A mission statement.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Mission extends TypedObject {

    /**
     * Default constructor.
     */
    public Mission() {
        super( null, Taxonomy.Mission );
    }
}
