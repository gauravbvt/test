// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import java.util.List;

/**
 * A resource that can be contacted.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - * ContactInfo
 */
public interface Contactable {

    /**
     * Return the list of contact informations.
     */
    List<ContactInfo> getContactInfos();
}
