package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 9:57:45 AM
 */
class Query {

    String name
    Object[] arguments

    Query(String s, List args) {
        name = s
        arguments = args as Object[]
    }

    Query(String s) {
        name = s
        arguments = [] as Object[]
    }

    Query(String s, def arg) {
        name = s
        arguments = [arg] as Object[]
    }

    Query(String s, def arg1, def arg2) {
        name = s
        arguments = [arg1, arg2] as Object[]
     }

    Query(String s, def arg1, def arg2, def arg3) {
        name = s
        arguments = [arg1, arg2, arg3] as Object[]
    }

    static def execute(Ref element, String s) {
        return QueryManager.instance().execute(element, new Query(s))
    }

    static def execute(Ref element, String s, List args) {
        return QueryManager.instance().execute(element, new Query(s, args))
    }

    static def execute(Ref element, String s, def arg) {
        return QueryManager.instance().execute(element, new Query(s, arg))
    }

    static def execute(Ref element, String s, def arg1, def arg2) {
        return QueryManager.instance().execute(element, new Query(s, arg1, arg2))
    }

    static def execute(Ref element, String s, def arg1, def arg2, def arg3) {
        return QueryManager.instance().execute(element, new Query(s, arg1, arg2, arg3))
    }

    def execute(Ref element) {
       return QueryManager.instance().execute(element, this)
    }

    String toString() {
        return "Query $name with $arguments"
    }


    boolean equals(Object obj) {
        if (!obj instanceof Query) return false
        Query qe = (Query) obj
        if (this.name != qe.name) return false
        if (this.arguments.size() != qe.arguments.size()) return false
        arguments.eachWithIndex {arg, i ->
            if (this.arguments[i] != qe.arguments[i]) return false
        }
        return true
    }

    int hashCode() {
        int hash = 1
        hash = hash * 31 + this.name.hashCode()
        arguments.each {hash = hash * 31 + it.hashCode() }
        return hash
    }

}