package com.mindalliance.channels.playbook.support.drools;

import org.drools.WorkingMemory;
import org.drools.WorkingMemoryEventManager;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AfterFunctionRemovedEvent;
import org.drools.event.AfterPackageAddedEvent;
import org.drools.event.AfterPackageRemovedEvent;
import org.drools.event.AfterRuleAddedEvent;
import org.drools.event.AfterRuleBaseLockedEvent;
import org.drools.event.AfterRuleBaseUnlockedEvent;
import org.drools.event.AfterRuleRemovedEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.BeforeFunctionRemovedEvent;
import org.drools.event.BeforePackageAddedEvent;
import org.drools.event.BeforePackageRemovedEvent;
import org.drools.event.BeforeRuleAddedEvent;
import org.drools.event.BeforeRuleBaseLockedEvent;
import org.drools.event.BeforeRuleBaseUnlockedEvent;
import org.drools.event.BeforeRuleRemovedEvent;
import org.drools.event.ObjectInsertedEvent;
import org.drools.event.ObjectRetractedEvent;
import org.drools.event.ObjectUpdatedEvent;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleFlowCompletedEvent;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.event.RuleFlowStartedEvent;
import org.drools.event.WorkingMemoryEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redirect drools logging to a log4j logger.
 */
public class LoggerAdapter
        implements WorkingMemoryEventListener, AgendaEventListener, RuleFlowEventListener, RuleBaseEventListener {

    private Logger memoryLogger;
    private Logger agendaLogger;
    private Logger ruleBaseLogger;
    private Logger ruleFlowLogger;

    public LoggerAdapter(
            Logger mainLogger, WorkingMemoryEventManager session ) {
        if ( mainLogger == null )
            throw new IllegalArgumentException();
        String name = mainLogger.getName();
        memoryLogger = LoggerFactory.getLogger( name + ".memory" );
        agendaLogger = LoggerFactory.getLogger( name + ".agenda" );
        ruleBaseLogger = LoggerFactory.getLogger( name + ".ruleBase" );
        ruleFlowLogger = LoggerFactory.getLogger( name + ".ruleFlow" );
        session.addEventListener( (WorkingMemoryEventListener) this );
        session.addEventListener( (AgendaEventListener) this );
        session.addEventListener( (RuleFlowEventListener) this );
        session.addEventListener( (RuleBaseEventListener) this );
    }

    public void objectInserted( ObjectInsertedEvent event ) {
        if ( memoryLogger.isDebugEnabled() )
            memoryLogger.debug( "Inserted: " + event.getObject() );
    }

    public void objectUpdated( ObjectUpdatedEvent event ) {
        if ( memoryLogger.isDebugEnabled() )
            memoryLogger.debug( "Updated: " + event.getObject() );
    }

    public void objectRetracted( ObjectRetractedEvent event ) {
        if ( memoryLogger.isDebugEnabled() )
            memoryLogger.debug( "Retracted: " + event.getOldObject() );
    }

    public void activationCreated(
            ActivationCreatedEvent event, WorkingMemory workingMemory ) {
        if ( agendaLogger.isDebugEnabled() )
            agendaLogger.debug(
                    "Created activation: " + event.getActivation() );
    }

    public void activationCancelled(
            ActivationCancelledEvent event, WorkingMemory workingMemory ) {
        if ( agendaLogger.isDebugEnabled() )
            agendaLogger.debug(
                    "Cancelled activation: " + event.getActivation() );
    }

    public void beforeActivationFired(
            BeforeActivationFiredEvent event, WorkingMemory workingMemory ) {
        if ( agendaLogger.isDebugEnabled() )
            agendaLogger.debug(
                    "Before activation firing: " + event.getActivation() );
    }

    public void afterActivationFired(
            AfterActivationFiredEvent event, WorkingMemory workingMemory ) {
        if ( agendaLogger.isDebugEnabled() )
            agendaLogger.debug(
                    "After activation firing: " + event.getActivation() );
    }

    public void agendaGroupPopped(
            AgendaGroupPoppedEvent event, WorkingMemory workingMemory ) {
        if ( agendaLogger.isDebugEnabled() )
            agendaLogger.debug( "Popped group: " + event.getAgendaGroup() );
    }

    public void agendaGroupPushed(
            AgendaGroupPushedEvent event, WorkingMemory workingMemory ) {
        if ( agendaLogger.isDebugEnabled() )
            agendaLogger.debug( "Pushed group: " + event.getAgendaGroup() );
    }

    public void ruleFlowStarted(
            RuleFlowStartedEvent event, WorkingMemory workingMemory ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "Started: " + event.getRuleFlowProcessInstance() );
    }

    public void ruleFlowCompleted(
            RuleFlowCompletedEvent event, WorkingMemory workingMemory ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "Completed: " + event.getRuleFlowProcessInstance() );
    }

    public void ruleFlowGroupActivated(
            RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "Activated group: " + event.getRuleFlowGroup() );
    }

    public void ruleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "Deactivated group: " + event.getRuleFlowGroup() );
    }

    public void beforePackageAdded( BeforePackageAddedEvent event ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "Before adding package: " + event.getPackage() );
    }

    public void afterPackageAdded( AfterPackageAddedEvent event ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "After adding package: " + event.getPackage() );
    }

    public void beforePackageRemoved( BeforePackageRemovedEvent event ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "Before removing package: " + event.getPackage() );
    }

    public void afterPackageRemoved( AfterPackageRemovedEvent event ) {
        if ( ruleFlowLogger.isDebugEnabled() )
            ruleFlowLogger.debug(
                    "After removing package: " + event.getPackage() );
    }

    public void beforeRuleBaseLocked( BeforeRuleBaseLockedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "Before locking rulebase: " + event.getRuleBase() );
    }

    public void afterRuleBaseLocked( AfterRuleBaseLockedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "After locking rulebase: " + event.getRuleBase() );
    }

    public void beforeRuleBaseUnlocked( BeforeRuleBaseUnlockedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "Before unlocking rulebase: " + event.getRuleBase() );
    }

    public void afterRuleBaseUnlocked( AfterRuleBaseUnlockedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "After unlocking rulebase: " + event.getRuleBase() );
    }

    public void beforeRuleAdded( BeforeRuleAddedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "Before adding rule in " + event.getRuleBase() + ": "
                    + event.getRule() );
    }

    public void afterRuleAdded( AfterRuleAddedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "After adding rule in " + event.getRuleBase() + ": "
                    + event.getRule() );
    }

    public void beforeRuleRemoved( BeforeRuleRemovedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "Before removing rule in " + event.getRuleBase() + ": "
                    + event.getRule() );
    }

    public void afterRuleRemoved( AfterRuleRemovedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "After removing rule in " + event.getRuleBase() + ": "
                    + event.getRule() );
    }

    public void beforeFunctionRemoved( BeforeFunctionRemovedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "Before removing function in " + event.getRuleBase() + ": "
                    + event.getRule() );
    }

    public void afterFunctionRemoved( AfterFunctionRemovedEvent event ) {
        if ( ruleBaseLogger.isDebugEnabled() )
            ruleBaseLogger.debug(
                    "After removing function in " + event.getRuleBase() + ": "
                    + event.getRule() );
    }
}
