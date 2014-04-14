/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Filterable communication commitments.
 */
public class Commitments implements Serializable, Iterable<Commitment> {

    private final Set<Commitment> commitments = new HashSet<Commitment>();

    private Place planLocale;

    public Commitments() {
    }

    public static Commitments all( QueryService queryService ) {
        return all( queryService, false );
    }


    public static Commitments all( QueryService queryService, Boolean includeToSelf ) {
        Commitments allCommitments = new Commitments( queryService.getPlanLocale() );
        allCommitments.addAll( queryService.findAllCommitments( includeToSelf ) );
        return allCommitments;
    }

    public static Commitments all( QueryService queryService, Boolean includeToSelf, Boolean includeUnknowns ) {
        Commitments allCommitments = new Commitments( queryService.getPlanLocale() );
        allCommitments.addAll( queryService.findAllCommitments( includeToSelf, includeUnknowns ) );
        return allCommitments;
    }



    public Commitments( QueryService queryService, Specable profile, Assignments assignments ) {
        planLocale = queryService.getPlanLocale();
        List<Flow> sharingFlows = queryService.findAllFlows(); //assignments.getSharingFlows();
        commitments.addAll( queryService.findAllCommitmentsOf( profile, assignments, sharingFlows ) );
        commitments.addAll( queryService.findAllCommitmentsTo( profile, assignments, sharingFlows ) );
    }

    public Commitments( QueryService queryService, List<Flow> flows ) {
        planLocale = queryService.getPlanLocale();
        for ( Flow flow : flows ) {
            commitments.addAll( queryService.findAllCommitments( flow ) );
        }
    }

    public Commitments( Place planLocale ) {
        this.planLocale = planLocale;
    }

    public Commitments of( Assignment assignment ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( assignment.equals( commitment.getCommitter() ) )
                result.add( commitment );

        return result;
    }

    public Commitments to( Assignment assignment ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( assignment.equals( commitment.getBeneficiary() ) )
                result.add( commitment );

        return result;
    }

    public Commitments to( Specable specable ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getBeneficiary().getResourceSpec().narrowsOrEquals( specable, planLocale ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments notTo( Specable specable ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( !commitment.getBeneficiary().getResourceSpec().narrowsOrEquals( specable, planLocale ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments notFrom( Specable specable ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( !commitment.getCommitter().getResourceSpec().narrowsOrEquals( specable, planLocale ) )
                result.add( commitment );
        }
        return result;
    }


    public Commitments from( Specable specable ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getCommitter().getResourceSpec().narrowsOrEquals( specable, planLocale ) )
                result.add( commitment );
        }
        return result;
    }



    public Commitments with( Flow flow ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( flow.equals( commitment.getSharing() ) )
                result.add( commitment );

        return result;
    }

    public Commitments withEoi( final String eoi ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( CollectionUtils.exists( commitment.getSharing().getEffectiveEois(), new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    return Matcher.same( ( (ElementOfInformation) object ).getContent(), eoi );
                }
            } ) )
                result.add( commitment );

        return result;
    }

    public Commitments withInfo( String info ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments )
            if ( Matcher.same( commitment.getSharing().getName(), info ) )
                result.add( commitment );

        return result;
    }


    public Commitments inSegment( Segment segment ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments ) {
            if ( segment == null || commitment.isInSegment( segment ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments inSituation( Phase.Timing timing, Event event, Place planLocale ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : commitments ) {
            if ( commitment.isInSituation( timing, event, planLocale ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments realizable( Analyst analyst, CollaborationModel collaborationModel, CommunityService communityService ) {
        Commitments result = new Commitments( planLocale );
        Iterator<Commitment> iterator = iterator();
        while ( iterator.hasNext() ) {
            Commitment commitment = iterator.next();
            if ( analyst.canBeRealized( commitment, collaborationModel, communityService ) )
                result.add( commitment );
        }
        return result;
    }


    public Commitments withEntityCommitting( ModelEntity entity, Place planLocale ) {
        Commitments result = new Commitments( planLocale );
        Iterator<Commitment> iterator = iterator();
        while ( iterator.hasNext() ) {
            Commitment commitment = iterator.next();
            if ( commitment.getCommitter().getResourceSpec().hasEntityOrNarrower( entity, planLocale ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments withEntityBenefiting( ModelEntity entity, Place planLocale ) {
        Commitments result = new Commitments( planLocale );
        Iterator<Commitment> iterator = iterator();
        while ( iterator.hasNext() ) {
            Commitment commitment = iterator.next();
            if ( commitment.getBeneficiary().getResourceSpec().hasEntityOrNarrower( entity, planLocale ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments benefiting( Assignment assignment ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getBeneficiary().equals( assignment ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments committing( Assignment assignment ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getCommitter().equals( assignment ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments toSelf() {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getSharing().isToSelf() )
                result.add( commitment );
        }
        return result;
    }

    public Commitments notifications() {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getSharing().isNotification() )
                result.add( commitment );
        }
        return result;
    }

    public Commitments requests() {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getSharing().isAskedFor() )
                result.add( commitment );
        }
        return result;
    }

    public Commitments notTriggeringToTarget() {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( !commitment.getSharing().isTriggeringToTarget() )
                result.add( commitment );
        }
        return result;
    }

    public Commitments notTriggeringToSource() {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( !commitment.getSharing().isTriggeringToSource() )
                result.add( commitment );
        }
        return result;
    }


    public Commitments provisioning( MaterialAsset materialAsset ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( !commitment.getSharing().getAssetConnections().provisioning().about( materialAsset ).isEmpty() )
                result.add( commitment );
        }
        return result;
    }

    public Commitments forwardingDemandFor( MaterialAsset materialAsset ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( commitment.getSharing().getAssetConnections().forwardsRequestFor( materialAsset ) )
                result.add( commitment );
        }
        return result;
    }

    public Commitments demanding( MaterialAsset materialAsset ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            if ( !commitment.getSharing().getAssetConnections().demanding().about( materialAsset ).isEmpty() )
                result.add( commitment );
        }
        return result;
    }

    public Commitments involvingButNotInitiatedBy( Assignment assignment ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
           Flow sharing = commitment.getSharing();
            if ( sharing.isNotification() ) {
                if ( commitment.getBeneficiary().equals( assignment ) ) {
                    result.add( commitment );
                }
            }  else {
                if ( commitment.getCommitter().equals( assignment ) ) {
                    result.add( commitment );
                }
            }
        }
        return result;
   }

    public Commitments initiatedBy( Assignment assignment ) {
        Commitments result = new Commitments( planLocale );
        for ( Commitment commitment : this ) {
            Flow sharing = commitment.getSharing();
            if ( sharing.isNotification() ) {
                if ( commitment.getCommitter().equals( assignment ) ) {
                    result.add( commitment );
                }
            }  else {
                if ( commitment.getBeneficiary().equals( assignment ) ) {
                    result.add( commitment );
                }
            }
        }
        return result;
    }

    public void add( Commitment commitment ) {
        commitments.add( commitment );
    }

    public void addAll( Commitments others ) {
        for ( Commitment commitment : others ) {
            commitments.add( commitment );
        }
    }

    public void addAll( List<Commitment> others ) {
        for ( Commitment commitment : others ) {
            commitments.add( commitment );
        }
    }

    @Override
    public Iterator<Commitment> iterator() {
        return commitments.iterator();
    }

    public boolean isEmpty() {
        return commitments.isEmpty();
    }

    public int size() {
        return commitments.size();
    }

    public List<Commitment> toList() {
        return new ArrayList<Commitment>( commitments );
    }
}

