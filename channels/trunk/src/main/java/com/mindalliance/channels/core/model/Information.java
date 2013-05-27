package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information = name, optional info product and elements.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 10:45 AM
 */
public class Information implements Serializable {

    private String name = "";
    private InfoProduct infoProduct;
    private List<ElementOfInformation> eois = new ArrayList<ElementOfInformation>();

    public Information() {
    }

    public Information( String name ) {
        this.name = name;
    }

    public Information( InfoProduct infoProduct ) {
        this.infoProduct = infoProduct;
    }

    public List<ElementOfInformation> getEois() {
        return eois;
    }

    public void setEois( List<ElementOfInformation> eois ) {
        this.eois = eois;
    }

    public String getName() {
        return infoProduct == null ? name : infoProduct.getName();
    }

    public void setName( String name ) {
        this.name = name;
    }

    public InfoProduct getInfoProduct() {
        return infoProduct;
    }

    public void setInfoProduct( InfoProduct infoProduct ) {
        this.infoProduct = infoProduct;
    }

    public void addEoi( ElementOfInformation eoi ) {
        eois.add( eoi );
    }

    public List<ElementOfInformation> getEffectiveEois() {
        List<ElementOfInformation> effective = new ArrayList<ElementOfInformation>( getEois() );
        if ( infoProduct != null ) {
            for (ElementOfInformation eoi : infoProduct.getEffectiveEois() ) {
                if ( !effective.contains( eoi ))
                    effective.add( eoi );
            }
        }
        Collections.sort( effective, new Comparator<ElementOfInformation>() {
            @Override
            public int compare( ElementOfInformation e1, ElementOfInformation e2 ) {
                return e1.getContent().compareTo( e2.getContent() );
            }
        });
        return effective;
    }

    @SuppressWarnings("unchecked")
    public List<String> getEffectiveEoiNames() {
        return (List<String>) CollectionUtils.collect(
                getEffectiveEois(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (ElementOfInformation) input ).getContent();
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<String> getLocalEoiNames() {
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
                                ( infoProduct == null || infoProduct.narrowsOrEquals( flow.getInfoProduct() )) &&
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
        if ( infoProduct != null )
            state.put( "infoProduct", infoProduct.getName() );
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
    public static Information fromState( Map<String, Object> state, QueryService queryService ) {
        Information info = new Information();
        info.setName( (String) state.get( "name" ) );
        String infoProductName = (String)state.get( "infoProduct" );
        if ( infoProductName != null ) {
            info.setInfoProduct( queryService.findOrCreateType( InfoProduct.class, infoProductName ) );
        }
        for ( ElementOfInformation eoi : (List<ElementOfInformation>) state.get( "eois" ) ) {
            info.addEoi( new ElementOfInformation( eoi ) );
        }
        return info;
    }

    public boolean isLocalEoi( ElementOfInformation eoi ) {
        return getLocalEoiNames().contains( eoi.getContent() );
    }

 }
