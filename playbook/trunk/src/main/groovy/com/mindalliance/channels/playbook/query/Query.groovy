package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 9:57:45 AM
 */
class Query implements Serializable {

    String name
    List arguments

    Query(String s) {
        name = s
        arguments = []
    }

    Query(String s, Object arg) {
        name = s
        arguments = [arg]
    }

    Query(String s, Object arg1, Object arg2) {
        name = s
        arguments = [arg1, arg2]
     }

    Query(String s, Object arg1, Object arg2, Object arg3) {
        name = s
        arguments = [arg1, arg2, arg3]
    }

    static Object execute(Object object, String s) {
        return QueryManager.instance().execute(object, new Query(s))
    }

    static Object execute(Object object, String s, List args) {
        return QueryManager.instance().execute(object, new Query(s, args))
    }

    static Object execute(Object object, String s, Object arg) {
        return QueryManager.instance().execute(object, new Query(s, arg))
    }

    static Object execute(Object object, String s, Object arg1, Object arg2) {
        return QueryManager.instance().execute(object, new Query(s, arg1, arg2))
    }

    static Object execute(Object object, String s, Object arg1, Object arg2, Object arg3) {
        return QueryManager.instance().execute(object, new Query(s, arg1, arg2, arg3))
    }

    def execute(Object object) {
       return QueryManager.instance().execute(object, this)
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
        arguments.each {arg ->
            if (arg) hash = hash * 31 + arg.hashCode()
        }
        return hash
    }

}