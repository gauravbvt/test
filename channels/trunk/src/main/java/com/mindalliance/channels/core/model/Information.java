package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
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
    private boolean published = false;

    public Information() {
    }

    public Information( String name ) {
        this.name = name;
    }

    public Information( InfoProduct infoProduct ) {
        this.infoProduct = infoProduct;
    }

    public Information( Flow flow ) {
        name = flow.getName();
        infoProduct = flow.getInfoProduct();
        eois = flow.getEffectiveEois();
        published = flow.isPublished();
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished( boolean published ) {
        this.published = published;
    }

    public void addEoi( ElementOfInformation eoi ) {
        eois.add( eoi );
    }

    public List<ElementOfInformation> getEffectiveEois() {
        List<ElementOfInformation> effective = new ArrayList<ElementOfInformation>( getEois() );
        if ( infoProduct != null ) {
            for ( ElementOfInformation eoi : infoProduct.getEffectiveEois() ) {
                if ( !effective.contains( eoi ) )
                    effective.add( eoi );
            }
        }
        Collections.sort( effective, new Comparator<ElementOfInformation>() {
            @Override
            public int compare( ElementOfInformation e1, ElementOfInformation e2 ) {
                return e1.getContent().compareTo( e2.getContent() );
            }
        } );
        return effective;
    }

    @SuppressWarnings( "unchecked" )
    // Sorts the names
    public List<String> getEffectiveEoiNames() {
        List<String> eoiNames = (List<String>) CollectionUtils.collect(
                getEffectiveEois(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (ElementOfInformation) input ).getContent();
                    }
                }
        );
        Collections.sort( eoiNames );
        return eoiNames;
    }

    @SuppressWarnings( "unchecked" )
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
                                ( infoProduct == null || infoProduct.narrowsOrEquals( flow.getInfoProduct() ) ) &&
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
        state.put( "published", isPublished() );
        return state;
    }

    private List<ElementOfInformation> copyEois() {
        List<ElementOfInformation> copy = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : eois ) {
            copy.add( new ElementOfInformation( eoi ) );
        }
        return copy;
    }

    @SuppressWarnings( "unchecked" )
    public static Information fromState( Map<String, Object> state, QueryService queryService ) {
        Information info = new Information();
        info.setName( (String) state.get( "name" ) );
        String infoProductName = (String) state.get( "infoProduct" );
        if ( infoProductName != null ) {
            info.setInfoProduct( queryService.findOrCreateType( InfoProduct.class, infoProductName ) );
        }
        for ( ElementOfInformation eoi : (List<ElementOfInformation>) state.get( "eois" ) ) {
            info.addEoi( new ElementOfInformation( eoi ) );
        }
        info.setPublished( (Boolean)state.get( "published" ) );
        return info;
    }

    public boolean isLocalEoi( ElementOfInformation eoi ) {
        return getLocalEoiNames().contains( eoi.getContent() );
    }

    public boolean narrowsOrEquals( Information other ) {
        return Matcher.same( getName(), other.getName() )
                && other.getEOIsString( ",", "," ).contains( getEOIsString( ",", "," ) );
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Information ) {
            Information other = (Information) object;
            return Matcher.same( getName(), other.getName() )
                    && getEOIsString( ",", "," ).equals( other.getEOIsString( ",", "," ) );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * getName().hashCode();
        hash = hash + 31 * getEOIsString( ",", "," ).hashCode();
        return hash;
    }

    private String getEOIsString( String sep, String lastSep ) {
        return ChannelsUtils.listToString( getEffectiveEoiNames(), sep, lastSep );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getName() );
        if ( !getEffectiveEois().isEmpty() ) {
            sb.append( "(" )
                    .append( getEOIsString( ", ", " and " ) );
        }
        return sb.toString();
    }
}
