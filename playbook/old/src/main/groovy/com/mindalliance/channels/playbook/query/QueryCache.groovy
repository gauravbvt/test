package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 19, 2008
 * Time: 7:49:41 PM
 */
class QueryCache implements Serializable {

    private Map cache = new HashMap()

    Set getCachedExecutions() {
        return cache.keySet()
    }

    def fromCache(QueryExecution execution) {
        def cached = cache[execution]
        if (cached != null) cached == BeanImpl.makeClone(cached)
        return cached
    }

    void toCache(QueryExecution execution, def results) {
        cache.put(execution, results)
    }

    int size() {
        return cache.findAll{qe, res -> res != null }.size()
    }


    void cleanup(String queryName) {
        Collection dirtyExecs = cache.keySet().findAll {qe -> qe.query.name == queryName}
        dirtyExecs.each {qe -> cache.put(qe, null)}
    }

    void clear() {
        cache = new HashMap()
    }

}