package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.command.MappedList;
import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
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
    private List<StepGuard> stepGuards = new ArrayList<StepGuard>();
    private String confirmationSignature;
    private boolean confirmationPending;

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
        return actionSteps.isEmpty()
                && localConditions.isEmpty()
                && stepOrders.isEmpty()
                && stepGuards.isEmpty();
    }

    public List<Step> listEffectiveSteps() {
        List<Step> effectiveSteps = new ArrayList<Step>();
        effectiveSteps.addAll( getActionSteps() );
        effectiveSteps.addAll( listCommunicationSteps() );
        return effectiveSteps;
    }

    public List<Condition> listEffectiveConditions() {
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.addAll( getLocalConditions() );
        conditions.addAll( listEventTimingConditions() );
        conditions.addAll( listGoalConditions() );
        return conditions;
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
    public List<StepGuard> listEffectiveStepGuards( final List<Step> steps, final List<Condition> conditions ) {
        return (List<StepGuard>) CollectionUtils.select(
                getStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).isEffective( steps, conditions );
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

    public void addStepGuard( StepGuard stepGuarding ) {
        confirmationSignature = null;
        stepGuards.add( stepGuarding );
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
        }
        else
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
        return hash;
    }

    // Communication steps


    private List<CommunicationStep> listCommunicationSteps() {
        List<CommunicationStep> communicationSteps = new ArrayList<CommunicationStep>();
        for ( Flow sharing : part.getAllSharingReceives() ) {
            if ( !sharing.isTriggeringToTarget() && sharing.isAskedFor() ) {
                communicationSteps.add( new CommunicationStep( sharing, false ) );
            }
        }
        for ( Flow sharing : part.getAllSharingSends() ) {
            if ( sharing.isNotification() ) {
                communicationSteps.add( new CommunicationStep( sharing, false ) );
            } else if ( sharing.isTriggeringToSource() ) {
                communicationSteps.add( new CommunicationStep( sharing, true ) );
            }
        }
        return communicationSteps;
    }

    public Step derefStep( String stepRef ) {
        return ActionStep.isActionStepRef( stepRef )
                ? findActionStep( stepRef )
                : findCommunicationStep( stepRef );
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


    public Condition deRefCondition( String conditionRef ) {
        return EventTimingCondition.isEventTimingRef( conditionRef )
                ? findEventTimingCondition( conditionRef )
                : GoalCondition.isGoalRef( conditionRef )
                ? findGoalCondition( conditionRef )
                : findLocalCondition( conditionRef );
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

    // Event timing conditions

    private List<EventTimingCondition> listEventTimingConditions() {
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
        return eventTimingConditions;
    }

    // Goal conditions


    private List<GoalCondition> listGoalConditions() {
        List<GoalCondition> goalConditions = new ArrayList<GoalCondition>();
        for ( Goal goal : part.getGoalsAchieved() ) {
            goalConditions.add( new GoalCondition( goal ) );
        }
        return goalConditions;
    }

    public List<Condition> listConditionsFor( Step step ) {
        List<Condition> stepConditions = new ArrayList<Condition>();
        String stepRef = step.getRef();
        for ( StepGuard stepGuard : getStepGuards() ) {
            if ( stepGuard.getStepRef().equals( stepRef ) ) {
                Condition condition = deRefCondition( stepGuard.getConditionRef() );
                if ( condition != null )
                    stepConditions.add( condition );
            }
        }
        return stepConditions;
    }


    public List<Condition> listConditionsFor( Step step, boolean positive ) {
        List<Condition> stepConditions = new ArrayList<Condition>();
        String stepRef = step.getRef();
        for ( StepGuard stepGuard : getStepGuards() ) {
            if ( stepGuard.isPositive() == positive && stepGuard.getStepRef().equals( stepRef ) ) {
                Condition condition = deRefCondition( stepGuard.getConditionRef() );
                if ( condition != null )
                    stepConditions.add( condition );
            }
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
                if ( preS1.isEmpty() && !preS2.isEmpty() )
                    return -1;
                if ( !preS1.isEmpty() && preS2.isEmpty() )
                    return 1;
                if ( preS1.contains( s2 ) && !preS2.contains( s1 ) )
                    return -1;
                if ( preS2.contains( s1 ) && !preS1.contains( s2 ) )
                    return 1;
                if ( preS1.size() < preS2.size() ) return -1;
                if ( preS2.size() < preS1.size() ) return 1;
                return s1.getLabel().compareTo( s2.getLabel() );
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
                getStepGuards(),
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
                getStepGuards(),
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
                getStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        StepGuard stepGuard = (StepGuard) object;
                        return stepGuard.isPositive() == positive && stepGuard.getStepRef().equals( stepRef );
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
        map.put( "stepGuards", new MappedList<StepGuard>( getStepGuards() ) );
    }

    @SuppressWarnings( "unchecked" )
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
}
