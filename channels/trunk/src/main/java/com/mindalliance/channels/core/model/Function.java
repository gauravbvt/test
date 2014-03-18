package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.asset.AssetConnectable;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.AssetConnections;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A standard function to be implemented by tasks.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/21/13
 * Time: 10:25 AM
 */
public class Function extends ModelEntity implements AssetConnectable {

    public static Function UNKNOWN;

    /**
     * Name of unknown function.
     */
    public static String UnknownName = "(unknown)";
    /**
     * Info needed to do function.
     */
    private List<Information> infoNeeded = new ArrayList<Information>();
    /**
     * Info shareable from doing function.
     */
    private List<Information> infoAcquired = new ArrayList<Information>();
    /**
     * Kinds of goals achievable from doing function.
     */
    private List<Objective> objectives = new ArrayList<Objective>();

    private AssetConnections assetConnections = new AssetConnections();


    public Function() {
    }

    public Function( String name ) {
        super( name );
    }

    @Override
    public boolean isInvolvedIn( Assignments allAssignments, Commitments allCommitments ) {
        return CollectionUtils.exists(
                allAssignments.getAssignments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Function function = ( (Assignment) object ).getPart().getFunction();
                        return function != null && function.narrowsOrEquals( Function.this );
                    }
                }
        );
    }

    public static String classLabel() {
        return "functions";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives( List<Objective> objectives ) {
        this.objectives = objectives;
    }

    public void addObjective( Objective objective ) {
        if ( !objectives.contains( objective ) )
            objectives.add( objective );
    }

    public List<Information> getInfoAcquired() {
        return infoAcquired;
    }

    public void addInfoAcquired( Information info ) {
        if ( !getInfoAcquiredNames().contains( info.getName().toLowerCase() ) ) {
            infoAcquired.add( info );
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getInfoAcquiredNames() {
        return (List<String>) CollectionUtils.collect(
                getInfoAcquired(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Information) input ).getName().toLowerCase();
                    }
                }
        );
    }

    public void setInfoAcquired( List<Information> infoAcquired ) {
        this.infoAcquired = infoAcquired;
    }

    public void addInfoNeeded( Information info ) {
        if ( !getInfoNeedNames().contains( info.getName().toLowerCase() ) ) {
            infoNeeded.add( info );
        }
    }

    public List<Information> getInfoNeeded() {
        return infoNeeded;
    }

    public void setInfoNeeded( List<Information> infoNeeded ) {
        this.infoNeeded = infoNeeded;
    }

    @SuppressWarnings("unchecked")
    private List<String> getInfoNeedNames() {
        return (List<String>) CollectionUtils.collect(
                getInfoNeeded(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Information) input ).getName().toLowerCase();
                    }
                }
        );
    }

    @Override
    public boolean isUndefined() {
        return super.isUndefined()
                && getObjectives().isEmpty()
                && getInfoNeedNames().isEmpty()
                && getInfoAcquired().isEmpty();
    }

    public List<Information> getEffectiveInfoNeeded() {
        List<Information> effective = new ArrayList<Information>( getInfoNeeded() );
        List<String> names = getInfoNeedNames();
        for ( ModelEntity category : this.getAllTypes() ) {  // ordered bottom up
            for ( Information infoNeed : ( (Function) category ).getInfoNeeded() ) {
                if ( !names.contains( infoNeed.getName().toLowerCase() ) ) {
                    names.add( infoNeed.getName().toLowerCase() );
                    effective.add( infoNeed );
                }
            }
        }
        return effective;
    }

    public List<Information> getEffectiveInfoAcquired() {
        List<Information> effective = new ArrayList<Information>( getInfoAcquired() );
        List<String> names = getInfoAcquiredNames();
        for ( ModelEntity category : this.getAllTypes() ) {  // ordered bottom up
            for ( Information infoCapability : ( (Function) category ).getInfoAcquired() ) {
                if ( !names.contains( infoCapability.getName().toLowerCase() ) ) {
                    names.add( infoCapability.getName().toLowerCase() );
                    effective.add( infoCapability );
                }
            }
        }
        return effective;
    }

    public List<Objective> getEffectiveObjectives() {
        Set<Objective> effective = new HashSet<Objective>( getObjectives() );
        for ( ModelEntity category : this.getAllTypes() ) {
            effective.addAll( ( (Function) category ).getObjectives() );
        }
        return new ArrayList<Objective>( effective );
    }


    public boolean isEmpty() {
        return objectives.isEmpty() && infoAcquired.isEmpty() && infoNeeded.isEmpty();
    }

    public boolean implementedBy( Part part, QueryService queryService ) {
        return allObjectivesNotImplementedBy( part, queryService ).isEmpty()
                && allInfoNeedsNotImplementedBy( part ).isEmpty()
                && allInfoAcquiredNotImplementedBy( part ).isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<Objective> allObjectivesNotImplementedBy( final Part part, final QueryService queryService ) {
        return (List<Objective>) CollectionUtils.select(
                getEffectiveObjectives(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Objective) object ).implementedBy( part, queryService );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<Information> allInfoNeedsNotImplementedBy( final Part part ) {
        return (List<Information>) CollectionUtils.select(
                getEffectiveInfoNeeded(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Information info = (Information) object;
                        return !info.implementedEvenPartiallyBy( part.getNeeds() );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<Information> allInfoAcquiredNotImplementedBy( final Part part ) {
        return (List<Information>) CollectionUtils.select(
                getEffectiveInfoAcquired(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Information info = (Information) object;
                        return !info.implementedEvenPartiallyBy( part.getCapabilities() );
                    }
                }
        );
    }

    public Information findNeededInfoNamed( final String name ) {
        return (Information) CollectionUtils.find(
                getEffectiveInfoNeeded(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( ( (Information) object ).getName(), name );
                    }
                } );
    }

    public Information findAcquiredInfoNamed( final String name ) {
        return (Information) CollectionUtils.find(
                getEffectiveInfoAcquired(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( ( (Information) object ).getName(), name );
                    }
                } );
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return super.references( mo )
                || CollectionUtils.exists(
                getInfoNeeded(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ModelObject.areIdentical( ( (Information) object ).getInfoProduct(), mo );
                    }
                }
        ) || CollectionUtils.exists(
                getInfoAcquired(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ModelObject.areIdentical( ( (Information) object ).getInfoProduct(), mo );
                    }
                }
        );
    }


    // AssetConnectable


    @Override
    public boolean isCanStockAssets() {
        return false;
    }

    @Override
    public boolean isCanProduceAssets() {
        return true;
    }

    @Override
    public boolean isCanUseAssets() {
        return true;
    }

    @Override
    public boolean isCanProvisionAssets() {
        return true;
    }

    @Override
    public boolean isCanBeAssetDemand() {
        return false;
    }

    @Override
    public AssetConnections getAssetConnections() {
        return assetConnections;
    }

    public void addAssetConnection( AssetConnection assetConnection ) {
        assetConnections.add( assetConnection );
    }

}
