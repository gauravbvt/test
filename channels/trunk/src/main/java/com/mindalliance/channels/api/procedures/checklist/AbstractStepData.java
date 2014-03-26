package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.Step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data for the collaboration act in the step of a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:07 PM
 */
public abstract class AbstractStepData implements Serializable {

    private ChecklistData checklist;
    private List<ConditionData> ifConditions;
    private List<ConditionData> unlessConditions;
    private List<OutcomeData> outcomes;
    private Step step;
    private List<Integer> prerequisites;

    public AbstractStepData() {
        // required
    }

    public AbstractStepData( Step step,
                             ChecklistData checklist,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        this.step = step;
        this.checklist = checklist;
        initData( serverUrl, communityService, user );
    }

    public boolean isActionStep() {
        return step.isActionStep();
    }

    public boolean isCommunicationStep() {
        return step.isCommunicationStep();
    }

    protected void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        prerequisites = getChecklist().prerequisiteIndicesOfStep( step );
        initConditions( serverUrl, communityService, user );
        initOutcomes( serverUrl, communityService, user );
    }

    private void initConditions( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        ifConditions = new ArrayList<ConditionData>(  );
        for ( Condition condition : getChecklist().checklist().listConditionsFor( step, true ) ) {
            ifConditions.add( makeConditionData( condition, serverUrl, communityService, user ) );
        }
        unlessConditions = new ArrayList<ConditionData>(  );
        for ( Condition condition : getChecklist().checklist().listConditionsFor( step, false ) ) {
            unlessConditions.add( makeConditionData( condition, serverUrl, communityService, user ) );
        }
    }

    private ConditionData makeConditionData(
            Condition condition,
            String serverUrl,
            CommunityService communityService,
            ChannelsUser user ) {
        if ( condition.isLocalCondition() )
            return new LocalConditionData( condition, serverUrl, communityService, user );
         else if ( condition.isEventTimingCondition() )
            return new EventTimingConditionData( condition, serverUrl, communityService, user );
         else if ( condition.isGoalCondition() )
            return new GoalConditionData(condition, serverUrl, communityService, user );
        else if ( condition.isNeedSatisfiedCondition() )
            return new NeedSatisfiedConditionData(condition, serverUrl, communityService, user );
        else if ( condition.isTaskFailedCondition() )
            return new TaskFailedConditionData(condition, serverUrl, communityService, user );
        else if ( condition.isAssetAvailableCondition() ) {
            return new AssetAvailableConditionData( condition, serverUrl, communityService, user );
        }
        else
            throw new RuntimeException( "Unknown condition" );
    }

    private void initOutcomes( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        outcomes = new ArrayList<OutcomeData>(  );
        for ( Outcome outcome : getChecklist().checklist().listOutcomesFor( step ) ) {
            outcomes.add( makeOutcomeData( outcome, serverUrl, communityService, user ) );
        }
    }

    private OutcomeData makeOutcomeData( Outcome outcome,
                                         String serverUrl,
                                         CommunityService communityService,
                                         ChannelsUser user ) {
        if ( outcome.isGoalAchievedOutcome() ) {
            return new GoalAchievedOutcomeData( outcome, serverUrl, communityService, user );
        } else if ( outcome.isEventTimingOutcome() ) {
            return new EventTimingOutcomeData( outcome, serverUrl, communityService, user );
        } else if ( outcome.isCapabilityCreatedOutcome() ) {
            return new CapabilityCreatedOutcomeData( outcome, serverUrl, communityService, user );
        } else
            throw new RuntimeException( "Unknown outcome" );
    }

    public List<ConditionData> getIfConditions() {
        return ifConditions;
    }

    public List<ConditionData> getUnlessConditions() {
        return unlessConditions;
    }

    public List<OutcomeData> getOutcomes() {
        return outcomes;
    }

    public List<Integer> getPrerequisites() {
        return prerequisites;
    }

    public Step getStep() {
        return step;
    }

    public ChecklistData getChecklist() {
        return checklist;
    }

    public String getLabel() {
        return getStep().getLabel();
    }

    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allEventIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allEventIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allEventIds() );
        }
        return ids;
    }

    public Set<Long> allPhaseIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allPhaseIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allPhaseIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allPhaseIds() );
        }
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allOrganizationIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allOrganizationIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allOrganizationIds() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allActorIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allActorIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds(){
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allRoleIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allRoleIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allRoleIds() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allPlaceIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allPlaceIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allPlaceIds() );
        }
        return ids;
    }

    public Set<Long> allMediumIds() {
        return new HashSet<Long>(  );
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allInfoProductIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allInfoProductIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allInfoProductIds() );
        }
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        return new HashSet<Long>(  );
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for (ConditionData conditionData : getIfConditions() ) {
            ids.addAll( conditionData.allAssetIds() );
        }
        for (ConditionData conditionData : getUnlessConditions() ) {
            ids.addAll( conditionData.allAssetIds() );
        }
        for (OutcomeData outcomeData : getOutcomes() ) {
            ids.addAll( outcomeData.allAssetIds() );
        }
        return ids;
    }
}
