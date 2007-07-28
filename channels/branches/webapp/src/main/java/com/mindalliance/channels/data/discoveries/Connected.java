// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.discoveries;

import java.util.List;

/**
 * Has access to information resources.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Connected {

    /**
     * Return CanAccess assertions.
     */
    List<CanAccess> getCanAccessAssertions();

}
