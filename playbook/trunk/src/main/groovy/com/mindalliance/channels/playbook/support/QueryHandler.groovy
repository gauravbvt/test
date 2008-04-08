package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 8, 2008
* Time: 3:33:12 PM
*/
class QueryHandler implements Serializable {

    // TODO implement query caching

    List<Ref> executeQuery(Ref ref, String query, Map<String,Object>args) {
        Referenceable referenceable = ref.deref()
        // TODO -- SECURITY HOLE - make sure query is authorized
        List<Ref> results = (List<Ref>)referenceable."$query"(args)
    }

}