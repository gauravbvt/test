package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information = name and elements.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 10:45 AM
 */
public class Information implements Serializable {

    private String name = "";
    private List<ElementOfInformation> eois = new ArrayList<ElementOfInformation>();

    public Information() {
    }

    public Information( String name ) {
        this.name = name;
    }

    public List<ElementOfInformation> getEois() {
        return eois;
    }

    public void setEois( List<ElementOfInformation> eois ) {
        this.eois = eois;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void addEoi( ElementOfInformation eoi ) {
        eois.add( eoi );
    }

    @SuppressWarnings("unchecked")
    public List<String> getEoiNames() {
        return (List<String>) CollectionUtils.collect(
                getEois(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (ElementOfInformation) input ).getContent();
                    }
                }
        );
    }

    public boolean implementedFullyBy( List<Flow> flows ) {
        return CollectionUtils.exists(
                flows,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Flow flow = (Flow) object;
                        return Matcher.same( flow.getName(), getName() ) &&
                                ( getEois().isEmpty() ||
                                        !flow.getEois().isEmpty()
                                                && !CollectionUtils.exists(
                                                getEois(),
                                                new Predicate() {
                                                    @Override
                                                    public boolean evaluate( Object object ) {
                                                        final ElementOfInformation infoEoi = (ElementOfInformation) object;
                                                        return !CollectionUtils.exists(
                                                                flow.getEois(),
                                                                new Predicate() {
                                                                    @Override
                                                                    public boolean evaluate( Object object ) {
                                                                        ElementOfInformation flowEoi = (ElementOfInformation) object;
                                                                        return Matcher.same( infoEoi.getContent(), flowEoi.getContent() );
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        ) );
                    }
                }
        );

    }

    public boolean implementedEvenPartiallyBy( List<Flow> flows ) {
        return CollectionUtils.exists(
                flows,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Flow flow = (Flow) object;
                        return Matcher.same( flow.getName(), getName() );
                    }
                }
        );

    }


    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "name", getName() );
        state.put( "eois", copyEois() );
        return state;
    }

    private List<ElementOfInformation> copyEois() {
        List<ElementOfInformation> copy = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : eois ) {
            copy.add( new ElementOfInformation( eoi ) );
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static Information fromState( Map<String, Object> state ) {
        Information info = new Information();
        info.setName( (String) state.get( "name" ) );
        for ( ElementOfInformation eoi : (List<ElementOfInformation>) state.get( "eois" ) ) {
            info.addEoi( new ElementOfInformation( eoi ) );
        }
        return info;
    }

}
