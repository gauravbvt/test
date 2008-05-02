package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 9:58:34 AM
 */
class QueryExecution {

    Ref target
    Query query

    def execute() {
        def results = target.invokeMethod(query.name, query.arguments)
        return results
    }

    boolean equals(Object obj) {
        if (!obj instanceof QueryExecution) return false
        QueryExecution qe = (QueryExecution) obj
        if (this.target != qe.target) return false
        if (this.query != qe.query) return false
        return true
    }

    int hashCode() {
        int hash = 1
        hash = hash * 31 + this.target.hashCode()
        hash = hash * 31 + this.query.hashCode()
        return hash
    }

}