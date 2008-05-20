package com.mindalliance.channels.playbook.query
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 19, 2008
 * Time: 7:49:41 PM
 */
class QueryCache implements Serializable {

    private Map cache = [:]

    Set getCachedExecutions() {
        return cache.keySet()
    }

    def fromCache(QueryExecution execution) {
        return cache[execution]
    }

    void toCache(QueryExecution execution, def results) {
        cache.put(execution, results)
    }

    int size() {
        return cache.findAll{qe, res -> res != null }.size()
    }


    void cleanup(String queryName) {
        List dirtyExecs = cache.keySet().findAll {qe -> qe.query.name == queryName}
        dirtyExecs.each {qe -> cache.put(qe, null)}
    }

    void clear() {
        cache = [:]
    }

}