package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.command.MappedList;
import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.InfoCapability;
import com.mindalliance.channels.core.model.InfoNeed;
import com.mindalliance.channels.core.model.Mappable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 8:39 PM
 */
public class Checklist implements Serializable, Mappable {

    private Part part;

    // Explicit state  (full checklist state = explicit + implied )

    private List<ActionStep> actionSteps = new ArrayList<ActionStep>();
    private List<LocalCondition> localConditions = new ArrayList<LocalCondition>();
    private List<StepOrder> stepOrders = new ArrayList<StepOrder>();
    private List<StepGuard> stepGuards = new ArrayList<StepGuard>(); // explicit conditions on steps
    private List<StepOutcome> stepOutcomes = new ArrayList<StepOutcome>();
    private String confirmationSignature;
    private boolean confirmationPending;
    private TaskFailedCondition taskFailedCondition = new TaskFailedCondition();

    public Checklist() {
    }

    public Checklist( Part part ) {
        assert part != null;
        this.part = part;
    }

    public void setPart( Part part ) {
        assert part != null;
        this.part = part;
    }

    public boolean isEmpty() {
        return listEffectiveSteps().isEmpty();
    }

    public List<Step> listEffectiveSteps() {
        List<Step> effectiveSteps = new ArrayList<Step>();
        effectiveSteps.addAll( getActionSteps() );
        effectiveSteps.addAll( listCommunicationSteps() );
        effectiveSteps.addAll( listReceiptConfirmationSteps() );
        effectiveSteps.addAll( listSubTaskSteps() );
        for ( Step step : effectiveSteps ) {
            step.setId( effectiveSteps.indexOf( step ) );
        }
        return effectiveSteps;
    }

    public List<Condition> listEffectiveConditions() {
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.addAll( getLocalConditions() );
        conditions.addAll( listEventTimingConditions() );
        conditions.addAll( listGoalConditions() );
        conditions.addAll( listNeedSatisfiedConditions() );
        conditions.add( taskFailedCondition );
        for ( Condition condition : conditions ) {
            condition.setId( conditions.indexOf( condition ) );
        }
        return conditions;
    }

    public List<Outcome> listEffectiveOutcomes() {
        List<Outcome> outcomes = new ArrayList<Outcome>();
        outcomes.addAll( listEventTimingOutcomes() );
        outcomes.addAll( listGoalAchievedOutcomes() );
        outcomes.addAll( listCapabilityCreatedOutcomes() );
        for ( Outcome outcome : outcomes ) {
            outcome.setId( outcomes.indexOf( outcome ) );
        }
        return outcomes;
    }

    @SuppressWarnings("unchecked")
    public List<StepOrder> listEffectiveStepOrders( final List<Step> steps ) {
        return (List<StepOrder>) CollectionUtils.select(
                getStepOrders(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepOrder) object ).isEffective( steps );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<StepGuard> listEffectiveStepGuards( final boolean positive ) {
        return (List<StepGuard>) CollectionUtils.select(
                listEffectiveStepGuards( listEffectiveSteps(), listEffectiveConditions() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).isPositive() == positive;
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<StepGuard> listEffectiveStepGuards( final Step step, final boolean positive ) {
        return (List<StepGuard>) CollectionUtils.select(
                listEffectiveStepGuards( listEffectiveSteps(), listEffectiveConditions() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        StepGuard stepGuard = (StepGuard) object;
                        return stepGuard.isPositive() == positive
                                && stepGuard.getStepRef().equals( step.getRef() );
                    }
                }
        );
    }


    public List<StepGuard> listAllEffectiveStepGuards() {
        return listEffectiveStepGuards( listEffectiveSteps(), listEffectiveConditions() );
    }

    @SuppressWarnings("unchecked")
    public List<StepGuard> listEffectiveStepGuards( final List<Step> steps, final List<Condition> conditions ) {
        List<StepGuard> allStepGuards = (List<StepGuard>) CollectionUtils.select(
                getImmutableStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).isEffective( steps, conditions );
                    }
                }
        );
        allStepGuards.addAll( listAllImpliedStepGuards() );
        return allStepGuards;
    }

    private List<StepGuard> listAllImpliedStepGuards() {
        List<StepGuard> impliedStepGuards = new ArrayList<StepGuard>();
        for ( Step step : listEffectiveSteps() ) {
            if ( step.isCommunicationStep() ) {
                CommunicationStep communicationStep = (CommunicationStep) step;
                if ( communicationStep.getSharing().isIfTaskFails() ) {
                    StepGuard stepGuard = new StepGuard( taskFailedCondition, step, true ); // if task fails
                    impliedStepGuards.add( stepGuard );
                }
            }
        }
        return impliedStepGuards;
    }

    public boolean isImpliedStepGuard( StepGuard stepguard ) {
        return !getImmutableStepGuards().contains( stepguard );
    }

    public List<StepOutcome> listAllEffectiveStepOutcomes() {
        return listEffectiveStepOutcomes( listEffectiveSteps(), listEffectiveOutcomes() );
    }

    @SuppressWarnings("unchecked")
    public List<StepOutcome> listEffectiveStepOutcomes( final List<Step> steps, final List<Outcome> outcomes ) {
        return (List<StepOutcome>) CollectionUtils.select(
                getStepOutcomes(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepOutcome) object ).isEffective( steps, outcomes );
                    }
                }
        );
    }


    public Part getPart() {
        return part;
    }

    public List<ActionStep> getActionSteps() {
        return actionSteps;
    }

    public void addActionStep( ActionStep actionStep ) {
        confirmationSignature = null;
        actionSteps.add( actionStep );
    }

    public List<LocalCondition> getLocalConditions() {
        return localConditions;
    }

    public void addLocalCondition( LocalCondition localCondition ) {
        localConditions.add( localCondition );
    }

    public List<StepGuard> getStepGuards() {
        return stepGuards;
    }

    public List<StepGuard> getImmutableStepGuards() {
        return Collections.unmodifiableList( stepGuards );
    }

    public void addStepGuard( StepGuard stepGuarding ) {
        confirmationSignature = null;
        stepGuards.add( stepGuarding );
    }

    public List<StepOutcome> getStepOutcomes() {
        return stepOutcomes;
    }

    public void addStepOutcome( StepOutcome stepOutcome ) {
        confirmationSignature = null;
        stepOutcomes.add( stepOutcome );
    }

    public List<StepOrder> getStepOrders() {
        return stepOrders;
    }

    public void addStepOrder( StepOrder stepOrdering ) {
        confirmationSignature = null;
        stepOrders.add( stepOrdering );
    }

    private String computeSignature() {
        int signature = 1;
        List<Step> effectiveSteps = listEffectiveSteps();
        for ( Step step : effectiveSteps ) {
            signature = signature + step.hashCode();
        }
        for ( StepOrder stepOrder : listEffectiveStepOrders( effectiveSteps ) ) {
            signature = signature + stepOrder.hashCode();
        }
        for ( StepGuard stepGuard : listEffectiveStepGuards( effectiveSteps, listEffectiveConditions() ) ) {
            signature = signature + stepGuard.hashCode();
        }
        for ( StepOutcome stepOutcome : listEffectiveStepOutcomes( effectiveSteps, listEffectiveOutcomes() ) ) {
            signature = signature + stepOutcome.hashCode();
        }
        return Integer.toString( signature );

    }

    public String getConfirmationSignature() {
        return confirmationSignature;
    }

    public void setConfirmationSignature( String confirmationSignature ) {
        this.confirmationSignature = confirmationSignature;
    }

    public boolean isConfirmed() {
        if ( confirmationPending ) {
            confirm();
            confirmationPending = false;
        }
        return confirmationSignature != null && confirmationSignature.equals( computeSignature() );
    }

    public void setConfirmed( boolean val ) {
        if ( val ) {
            if ( part == null )    // must delay confirmation signing when loading from XML
                confirmationPending = true;
            else
                confirm();
        } else
            confirmationSignature = null;
    }

    public void confirm() {
        confirmationSignature = computeSignature();
    }

    public int hashCode() {
        int hash = 1;
        List<Step> effectiveSteps = listEffectiveSteps();
        for ( Step step : effectiveSteps ) {
            hash = hash * 31 + step.hashCode();
        }
        for ( StepOrder stepOrder : listEffectiveStepOrders( effectiveSteps ) ) {
            hash = hash * 31 + stepOrder.hashCode();
        }
        for ( StepGuard stepGuard : listEffectiveStepGuards( effectiveSteps, listEffectiveConditions() ) ) {
            hash = hash * 31 + stepGuard.hashCode();
        }
        for ( StepOutcome stepOutcome : listEffectiveStepOutcomes( effectiveSteps, listEffectiveOutcomes() ) ) {
            hash = hash * 31 + stepOutcome.hashCode();
        }
        return hash;
    }

    // Communication steps


    private List<CommunicationStep> listCommunicationSteps() {
        List<CommunicationStep> communicationSteps = new ArrayList<CommunicationStep>();
        for ( Flow sharing : part.getAllSharingReceives() ) {
            if ( !sharing.isTriggeringToTarget() && !sharing.isToSelf() && sharing.isAskedFor() ) {
                communicationSteps.add( new CommunicationStep( sharing, false ) );
            }
        }
        for ( Flow sharing : part.getAllSharingSends() ) {
            if ( !sharing.isToSelf() ) {
                if ( sharing.isNotification() ) {
                    communicationSteps.add( new CommunicationStep( sharing, false ) );
                } else if ( sharing.isTriggeringToSource() ) {
                    communicationSteps.add( new CommunicationStep( sharing, true ) );
                }
            }
        }
        return communicationSteps;
    }

    private List<ReceiptConfirmationStep> listReceiptConfirmationSteps() {
        List<ReceiptConfirmationStep> receiptConfirmationSteps = new ArrayList<ReceiptConfirmationStep>();
        for ( Flow sharing : part.getAllSharingReceives() ) {
            if ( sharing.isNotification()
                    && sharing.isTriggeringToTarget()
                    && sharing.isReceiptConfirmationRequested() ) {
                receiptConfirmationSteps.add( new ReceiptConfirmationStep( sharing ) );
            }
        }
        for ( Flow sharing : part.getAllSharingSends() ) {
            if ( sharing.isAskedFor()
                    && sharing.isTriggeringToSource()
                    && sharing.isReceiptConfirmationRequested() ) {
                receiptConfirmationSteps.add( new ReceiptConfirmationStep( sharing ) );
            }
        }
        return receiptConfirmationSteps;
    }

    private List<SubTaskStep> listSubTaskSteps() {
        List<SubTaskStep> subTaskSteps = new ArrayList<SubTaskStep>();
        for ( Flow sharing : part.getAllSharingReceives() ) {
            if ( SubTaskStep.isSubTask( sharing, false ) ) {
                subTaskSteps.add( new SubTaskStep( sharing, false ) );
            }
        }
        for ( Flow sharing : part.getAllSharingSends() ) {
            if ( SubTaskStep.isSubTask( sharing, true ) ) {
                subTaskSteps.add( new SubTaskStep( sharing, true ) );
            }
        }
        return subTaskSteps;
    }

    public Step derefStep( String stepRef ) {
        return ActionStep.isActionStepRef( stepRef )
                ? findActionStep( stepRef )
                : CommunicationStep.isCommunicationStepRef( stepRef )
                ? findCommunicationStep( stepRef )
                : SubTaskStep.isSubTaskStepRef( stepRef )
                ? findSubTaskStep( stepRef )
                : null;
    }

    private ActionStep findActionStep( final String stepRef ) {
        return (ActionStep) CollectionUtils.find(
                getActionSteps(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ActionStep) object ).getRef().equals( stepRef );
                    }
                }
        );
    }

    private CommunicationStep findCommunicationStep( final String stepRef ) {
        return (CommunicationStep) CollectionUtils.find(
                listCommunicationSteps(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (CommunicationStep) object ).getRef().equals( stepRef );
                    }
                }
        );
    }

    private SubTaskStep findSubTaskStep( final String stepRef ) {
        return (SubTaskStep) CollectionUtils.find(
                listSubTaskSteps(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (SubTaskStep) object ).getRef().equals( stepRef );
                    }
                }
        );
    }

    public Condition deRefCondition( String conditionRef ) {
        return EventTimingCondition.isEventTimingRef( conditionRef )
                ? findEventTimingCondition( conditionRef )
                : GoalCondition.isGoalRef( conditionRef )
                ? findGoalCondition( conditionRef )
                : NeedSatisfiedCondition.isNeedRef( conditionRef )
                ? findNeedStatisfiedCondition( conditionRef )
                : TaskFailedCondition.isTaskFailureCondition( conditionRef )
                ? findTaskFailedCondition( conditionRef )
                : findLocalCondition( conditionRef );
    }

    public Outcome deRefOutcome( String outcomeRef ) {
        return EventTimingOutcome.isEventOutcomeRef( outcomeRef )
                ? findEventOutcome( outcomeRef )
                : GoalAchievedOutcome.isGoalAchievedOutcomeRef( outcomeRef )
                ? findGoalAchievedOutcome( outcomeRef )
                : findCapabilityCreatedOutcome( outcomeRef );
    }

    @SuppressWarnings("unchecked")
    private Condition findEventTimingCondition( final String conditionRef ) {
        return (Condition) CollectionUtils.find(
                listEventTimingConditions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (EventTimingCondition) object ).getRef().equals( conditionRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private Condition findGoalCondition( final String conditionRef ) {
        return (Condition) CollectionUtils.find(
                listGoalConditions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (GoalCondition) object ).getRef().equals( conditionRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private Condition findNeedStatisfiedCondition( final String conditionRef ) {
        return (Condition) CollectionUtils.find(
                listNeedSatisfiedConditions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (NeedSatisfiedCondition) object ).getRef().equals( conditionRef );
                    }
                }
        );
    }

    private Condition findTaskFailedCondition( final String conditionRef ) {
        return taskFailedCondition;
    }

    @SuppressWarnings( "unchecked" )
    private Condition findLocalCondition( final String conditionRef ) {
        return (Condition) CollectionUtils.find(
                getLocalConditions(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (LocalCondition) object ).getRef().equals( conditionRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private Outcome findEventOutcome( final String outcomeRef ) {
        return (Outcome) CollectionUtils.find(
                listEventTimingOutcomes(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (EventTimingOutcome) object ).getRef().equals( outcomeRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private Outcome findGoalAchievedOutcome( final String outcomeRef ) {
        return (Outcome) CollectionUtils.find(
                listGoalAchievedOutcomes(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (GoalAchievedOutcome) object ).getRef().equals( outcomeRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private Outcome findCapabilityCreatedOutcome( final String outcomeRef ) {
        return (Outcome) CollectionUtils.find(
                listCapabilityCreatedOutcomes(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (CapabilityCreatedOutcome) object ).getRef().equals( outcomeRef );
                    }
                }
        );
    }


    // Event timing conditions

    public List<EventTimingCondition> listEventTimingConditions() {
        List<EventTimingCondition> eventTimingConditions = new ArrayList<EventTimingCondition>();
        if ( part.isTerminatesEventPhase() ) {
            EventTiming eventTiming = part.getSegment().getEventPhase().getEventTiming().getEventTimingAfterThis();
            if ( eventTiming != null )
                eventTimingConditions.add( new EventTimingCondition( eventTiming ) );
        }
        if ( part.getInitiatedEvent() != null ) {
            eventTimingConditions.add(
                    new EventTimingCondition(
                            new EventTiming( Phase.Timing.Concurrent, part.getInitiatedEvent() ) ) );
        }
        // If the task is initiated by an event, then, implicitly, all steps are conditional to it. So it's not included.
        return eventTimingConditions;
    }

    // Goal conditions

    public List<GoalCondition> listGoalConditions() {
        List<GoalCondition> goalConditions = new ArrayList<GoalCondition>();
        for ( Goal goal : part.getGoalsAchieved() ) {
            goalConditions.add( new GoalCondition( goal ) );
        }
        return goalConditions;
    }

    public List<NeedSatisfiedCondition> listNeedSatisfiedConditions() {
        List<NeedSatisfiedCondition> needSatisfiedConditions = new ArrayList<NeedSatisfiedCondition>();
        for ( InfoNeed need : part.getNonTriggeringInfoNeeds() ) {
            needSatisfiedConditions.add( new NeedSatisfiedCondition( need ) );
        }
        return needSatisfiedConditions;
    }

    public List<EventTimingOutcome> listEventTimingOutcomes() {
        List<EventTimingOutcome> eventTimingOutcomes = new ArrayList<EventTimingOutcome>();
        if ( part.isTerminatesEventPhase() ) {
            EventTiming eventTiming = part.getSegment().getEventPhase().getEventTiming().getEventTimingAfterThis();
            if ( eventTiming != null )
                eventTimingOutcomes.add( new EventTimingOutcome( eventTiming ) );
        }
        if ( part.getInitiatedEvent() != null ) {
            eventTimingOutcomes.add(
                    new EventTimingOutcome(
                            new EventTiming( Phase.Timing.Concurrent, part.getInitiatedEvent() ) ) );
        }
        return eventTimingOutcomes;
    }

    public List<GoalAchievedOutcome> listGoalAchievedOutcomes() {
        List<GoalAchievedOutcome> goalAchievedOutcomes = new ArrayList<GoalAchievedOutcome>();
        for ( Goal goal : part.getGoalsAchieved() ) {
            goalAchievedOutcomes.add( new GoalAchievedOutcome( goal ) );
        }
        return goalAchievedOutcomes;
    }

    public List<CapabilityCreatedOutcome> listCapabilityCreatedOutcomes() {
        List<CapabilityCreatedOutcome> capabilityCreatedOutcomes = new ArrayList<CapabilityCreatedOutcome>();
        for ( Flow capability : part.getCapabilities() ) {
            if ( capability.isPublished() )
                capabilityCreatedOutcomes.add( new CapabilityCreatedOutcome( new InfoCapability( capability ) ) );
        }
        return capabilityCreatedOutcomes;
    }


    public List<Condition> listConditionsFor( Step step ) {
        List<Condition> stepConditions = new ArrayList<Condition>();
        String stepRef = step.getRef();
        for ( StepGuard stepGuard : listEffectiveStepGuardsFor( step ) ) {
            if ( stepGuard.getStepRef().equals( stepRef ) ) {
                Condition condition = deRefCondition( stepGuard.getConditionRef() );
                if ( condition != null )
                    stepConditions.add( condition );
            }
        }
        return stepConditions;
    }

    public List<Outcome> listOutcomesFor( Step step ) {
        List<Outcome> outcomes = new ArrayList<Outcome>();
        String stepRef = step.getRef();
        for ( StepOutcome stepOutcome : getStepOutcomes() ) {
            if ( stepOutcome.getStepRef().equals( stepRef ) ) {
                Outcome outcome = deRefOutcome( stepOutcome.getOutcomeRef() );
                if ( outcome != null )
                    outcomes.add( outcome );
            }
        }
        return outcomes;
    }

    public List<Condition> listConditionsFor( Step step, boolean positive ) {
        List<Condition> stepConditions = new ArrayList<Condition>();
        String stepRef = step.getRef();
        for ( StepGuard stepGuard : listEffectiveStepGuardsFor( step, positive ) ) {
            Condition condition = deRefCondition( stepGuard.getConditionRef() );
            if ( condition != null )
                stepConditions.add( condition );
        }
        return stepConditions;
    }

    public List<StepOrder> listStepOrdersFor( Step step ) {
        List<StepOrder> stepOrders = new ArrayList<StepOrder>();
        String stepRef = step.getRef();
        for ( StepOrder stepOrder : getStepOrders() ) {
            if ( stepOrder.getStepRef().equals( stepRef ) ) {
                Step prerequisite = derefStep( stepOrder.getPrerequisiteStepRef() );
                if ( prerequisite != null )
                    stepOrders.add( stepOrder );
            }
        }
        return stepOrders;
    }

    public List<Step> listStepsJustBefore( Step step ) {
        List<Step> priorSteps = new ArrayList<Step>();
        for ( StepOrder stepOrder : listStepOrdersFor( step ) ) {
            Step prereqStep = derefStep( stepOrder.getPrerequisiteStepRef() );
            if ( prereqStep != null ) {
                priorSteps.add( prereqStep );
            }
        }
        return priorSteps;
    }

    public List<Step> listPrerequisiteStepsFor( Step step ) {
        return safePrerequisiteStepsFor( step, new HashSet<Step>() );
    }

    private List<Step> safePrerequisiteStepsFor( Step step, HashSet<Step> visited ) {
        visited.add( step );
        List<Step> prereqSteps = new ArrayList<Step>();
        for ( StepOrder stepOrder : listStepOrdersFor( step ) ) {
            Step prereqStep = derefStep( stepOrder.getPrerequisiteStepRef() );
            if ( prereqStep != null && !visited.contains( prereqStep ) ) {
                prereqSteps.add( prereqStep );
                prereqSteps.addAll( safePrerequisiteStepsFor( prereqStep, visited ) );
            }
        }
        return prereqSteps;
    }

    public boolean hasCircularPrerequisites( Step step ) {
        return safeHasCircularPrerequisites( step, step, new HashSet<Step>() );
    }

    private boolean safeHasCircularPrerequisites( Step baseStep, Step step, HashSet<Step> visited ) {
        visited.add( step );
        for ( StepOrder stepOrder : listStepOrdersFor( step ) ) {
            Step prereqStep = derefStep( stepOrder.getPrerequisiteStepRef() );
            if ( prereqStep != null ) {
                if ( prereqStep.equals( baseStep ) ) {
                    return true;
                } else if ( !visited.contains( prereqStep ) ) {
                    return safeHasCircularPrerequisites( baseStep, prereqStep, visited );
                }
            }
        }
        return false;
    }


    public void sort( List<Step> steps ) {
        Collections.sort( steps, new Comparator<Step>() {
            @Override
            public int compare( Step s1, Step s2 ) {
                List<Step> preS1 = listPrerequisiteStepsFor( s1 );
                List<Step> preS2 = listPrerequisiteStepsFor( s2 );
                if ( preS1.isEmpty() && preS2.isEmpty() )
                    return s1.getLabel().compareTo( s2.getLabel() );
                if ( preS1.isEmpty() && !preS2.isEmpty() )
                    return -1;
                if ( !preS1.isEmpty() && preS2.isEmpty() )
                    return 1;
                if ( preS2.contains( s1 ) && !preS1.contains( s2 ) )
                    return -1;
                if ( preS1.contains( s2 ) && !preS2.contains( s1 ) )
                    return 1;
                if ( preS1.size() < preS2.size() ) return -1;
                if ( preS2.size() < preS1.size() ) return 1;
                else return s1.getLabel().compareTo( s2.getLabel() );
            }
        } );
    }

    /**
     * Remove unreferenced local conditions and action steps.
     */
    public void cleanUp() {
        List<LocalCondition> unreferencedLocalConditions = new ArrayList<LocalCondition>();
        for ( LocalCondition localCondition : getLocalConditions() ) {
            if ( !isReferenced( localCondition ) )
                unreferencedLocalConditions.add( localCondition );
        }
        for ( LocalCondition localCondition : unreferencedLocalConditions ) {
            localConditions.remove( localCondition );
        }
    }

    private boolean isReferenced( ActionStep actionStep ) {
        final String ref = actionStep.getRef();
        return CollectionUtils.exists(
                getImmutableStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).getStepRef().equals( ref );
                    }
                }
        )
                || CollectionUtils.exists(
                getStepOrders(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        StepOrder stepOrder = (StepOrder) object;
                        return stepOrder.getPrerequisiteStepRef().equals( ref )
                                || stepOrder.getStepRef().equals( ref );
                    }
                }
        );
    }

    private boolean isReferenced( LocalCondition localCondition ) {
        final String ref = localCondition.getRef();
        return CollectionUtils.exists(
                getImmutableStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).getConditionRef().equals( ref );
                    }
                }
        );
    }

    public boolean hasGuards( final Step step, final boolean positive ) {
        final String stepRef = step.getRef();
        return CollectionUtils.exists(
                listEffectiveStepGuards( positive ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        StepGuard stepGuard = (StepGuard) object;
                        return stepGuard.isPositive() == positive && stepGuard.getStepRef().equals( stepRef );
                    }
                }
        );
    }

    public boolean hasOutcomes( final Step step ) {
        final String stepRef = step.getRef();
        return CollectionUtils.exists(
                listAllEffectiveStepOutcomes(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        StepOutcome stepOutcome = (StepOutcome) object;
                        return stepOutcome.getStepRef().equals( stepRef );
                    }
                }
        );

    }

    public boolean hasPrerequisites( Step step ) {
        final String stepRef = step.getRef();
        return CollectionUtils.exists(
                getStepOrders(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        StepOrder stepOrder = (StepOrder) object;
                        return stepOrder.getStepRef().equals( stepRef );
                    }
                }
        );
    }

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "part", new ModelObjectRef( part ) );
        if ( confirmationSignature != null )
            map.put( "confirmationSignature", confirmationSignature );
        map.put( "actionSteps", new MappedList<ActionStep>( getActionSteps() ) );
        map.put( "localConditions", new MappedList<LocalCondition>( getLocalConditions() ) );
        map.put( "stepOrders", new MappedList<StepOrder>( getStepOrders() ) );
        map.put( "stepGuards", new MappedList<StepGuard>( getImmutableStepGuards() ) );
    }

    @SuppressWarnings("unchecked")
    public List<StepOutcome> listEffectiveStepOutcomesFor( Step step ) {
        final String stepRef = step.getRef();
        return (List<StepOutcome>) CollectionUtils.select(
                listAllEffectiveStepOutcomes(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepOutcome) object ).getStepRef().equals( stepRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<StepGuard> listEffectiveStepGuardsFor( Step step, boolean positive ) {
        final String stepRef = step.getRef();
        return (List<StepGuard>) CollectionUtils.select(
                listEffectiveStepGuards( positive ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).getStepRef().equals( stepRef );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<StepGuard> listEffectiveStepGuardsFor( Step step ) {
        final String stepRef = step.getRef();
        return (List<StepGuard>) CollectionUtils.select(
                listAllEffectiveStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).getStepRef().equals( stepRef );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public List<Step> listStepsWithPrerequisite( final Step step ) {
        return (List<Step>) CollectionUtils.select(
                listEffectiveSteps(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return listPrerequisiteStepsFor( (Step) object ).contains( step );
                    }
                }
        );
    }
}
