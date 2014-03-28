package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A collection of communication commitments within a plan community.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/3/13
 * Time: 4:07 PM
 */
public class CommunityCommitments implements Iterable<CommunityCommitment>, Serializable {

    private final Set<CommunityCommitment> commitments = new HashSet<CommunityCommitment>();

    private Place locale;

    public CommunityCommitments( Place locale ) {
        this.locale = locale;
    }

    public void add( CommunityCommitment commitment ) {
        commitments.add( commitment );
    }


    public void addAll( CommunityCommitments commitments ) {
        for ( CommunityCommitment commitment : commitments ) {
            add( commitment );
        }
    }

    public CommunityCommitments benefiting( CommunityAssignment assignment ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.getBeneficiary().equals( assignment ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments committing( CommunityAssignment assignment ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.getCommitter().equals( assignment ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments toSelf() {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.isToSelf() )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments notToSelf() {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( !commitment.isToSelf() )
                result.add( commitment );
        }
        return result;
    }



    public CommunityCommitments notifications() {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.getSharing().isNotification() )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments requests() {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.getSharing().isAskedFor() )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments notTriggeringToTarget() {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( !commitment.getSharing().isTriggeringToTarget() )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments notTriggeringToSource() {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( !commitment.getSharing().isTriggeringToSource() )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments notFrom( Agent agent ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( !commitment.getCommitter().getAgent().equals( agent ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments notTo( Agent agent ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( !commitment.getBeneficiary().getAgent().equals( agent ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments to( Agent agent ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.getBeneficiary().getAgent().equals( agent ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments from( Agent agent ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( commitment.getCommitter().getAgent().equals( agent ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments withFlows( List<Flow> flows ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment commitment : this ) {
            if ( flows.contains( commitment.getSharing() ) )
                result.add( commitment );
        }
        return result;
    }

    public CommunityCommitments inSituation( Phase.Timing timing, Event event, Place planLocale ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment communityCommitment : this ) {
            if ( communityCommitment.isInSituation( timing, event, planLocale ) )
                result.add( communityCommitment );
        }
        return result;
    }

    public CommunityCommitments satisfying( Requirement requirement, CommunityService communityService ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment communityCommitment : this ) {
            if ( requirement.satisfiedBy( communityCommitment, communityService ) )
                result.add( communityCommitment );
        }
        return result;
    }

    public CommunityCommitments canBeRealized( CommunityService communityService ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment communityCommitment : this ) {
            boolean realizable = communityService.getAnalyst()
                    .findRealizabilityProblems(
                            communityService.getPlan(),
                            communityCommitment.getCommitment(),
                            communityService ).isEmpty();
            if ( realizable ) {
                result.add( communityCommitment );
            }
        }
        return result;
    }

    public CommunityCommitments demanding( MaterialAsset asset ) {
        CommunityCommitments result = new CommunityCommitments( locale );
        for ( CommunityCommitment communityCommitment : this ) {
            if ( !communityCommitment.getSharing().getAssetConnections().demanding().about( asset ).isEmpty() ) {
                result.add( communityCommitment );
            }
        }
        return result;
    }




    ////////////////////////////

    @Override
    public Iterator<CommunityCommitment> iterator() {
        return commitments.iterator();
    }

    public int size() {
        return commitments.size();
    }

    public boolean isEmpty() {
        return commitments.isEmpty();
    }

    public List<CommunityCommitment> toList() {
        return new ArrayList<CommunityCommitment>( commitments );
    }

 }
