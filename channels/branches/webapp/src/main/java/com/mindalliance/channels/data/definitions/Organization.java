// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import java.util.List;

/**
 * A generic organization.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @see com.mindalliance.channels.data.profiles.OrganizationImpl
 */
public interface Organization extends Typed {

    /**
     * Get all parent organizations.
     */
    List<Organization> getParents();

}
