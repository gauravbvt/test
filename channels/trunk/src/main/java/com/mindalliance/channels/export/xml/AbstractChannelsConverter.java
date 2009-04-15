package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Abstract XStream converter base class for Channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 13, 2009
 * Time: 3:52:08 PM
 */
public abstract class AbstractChannelsConverter implements Converter {
    /**
     * Get Data Query Object
     *
     * @return a data query object
     */
    protected DataQueryObject getDqo() {
        return Project.getProject().getDqo();
    }

    /**
     * List of external flows that could not be connected but were made into internal connector flows.
     * XStream's API did not allow using a "global" DataHolder for this purpose, at least not in an obvious way.
     */
    private static ThreadLocal brokenFlows = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new ArrayList<Flow>();
        }
    };

    /**
     * Return broken external flows in context.
     *
     * @return a list of connector flows
     */
    @SuppressWarnings( "unchecked" )
    protected List<Flow> getBrokenExternalFlows() {
        return (List<Flow>) brokenFlows.get();
    }

    /**
     * Make a substitution in the idmap
     *
     * @param previous    an identifiable
     * @param replacement an identifiable
     * @param idMap       a map
     */
    protected void replaceInIdMap( Identifiable previous, Identifiable replacement, Map<String, Long> idMap ) {
        for ( Map.Entry<String, Long> entry : idMap.entrySet() ) {
            if ( entry.getValue() == previous.getId() ) {
                entry.setValue( replacement.getId() );
                return;
            }
        }
    }


}
