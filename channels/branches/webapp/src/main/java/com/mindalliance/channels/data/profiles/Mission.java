// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.definitions.TypedObject;

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
