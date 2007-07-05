// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * A query in some language.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Query implements Serializable {

    /**
     * Supported languages.
     */
    enum Language {
        /** JxPath support. */
        JXPATH,

        /** OGNL support. */
        OGNL
    };

    private Language language;
    private String expression;

    /**
     * Default constructor.
     */
    public Query() {
    }

    /**
     * Iterate on results.
     * @param context the query's starting point
     * @param bindings other bindings
     */
    public Iterator findAll( Object context, Map bindings ) {
        // TODO
        return null;
    }

    /**
     * Return the first result of a query.
     * @param context
     * @param context the query's starting point
     * @param bindings other bindings
     */
    public Object findOne( Object context, Map bindings ) {
        // TODO
        return null;
    }

    /**
     * Return the expression.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Set the expression.
     * @param expression the expression to set
     */
    public void setExpression( String expression ) {
        this.expression = expression;
    }

    /**
     * Return the language.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Set the language.
     * @param language the language to set
     */
    public void setLanguage( Language language ) {
        this.language = language;
    }
}
