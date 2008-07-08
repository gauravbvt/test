package com.mindalliance.channels.playbook.support
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 11:20:04 AM
 */

import org.drools.rule.Package
import org.drools.RuleBase
import org.drools.RuleBaseFactory
import com.mindalliance.channels.playbook.support.drools.PackageLoader
import org.drools.StatefulSession
import org.drools.event.DebugAgendaEventListener
import org.drools.event.DebugWorkingMemoryEventListener
import org.drools.audit.WorkingMemoryFileLogger
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.ref.Ref
import org.drools.FactHandle
import com.mindalliance.channels.playbook.ref.Referenceable
import org.apache.log4j.Logger
import org.drools.RuleBaseConfiguration;


class RuleBaseSession implements Serializable {

    static final String LOG_PATH = "rules_log/firing"

    private String rulesPackageName
    private StatefulSession session
    private WorkingMemoryFileLogger logger
    private Map<Ref, FactHandle> factHandles = new HashMap<Ref, FactHandle>()
    private boolean needsFiring = false

    RuleBaseSession(String rulesPackageName) {
        this.rulesPackageName = rulesPackageName
        initialize()
    }

    private void initialize() {
        Package pkg = PackageLoader.loadPackage(this.class, rulesPackageName)
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setShadowProxy( false );
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf )
        ruleBase.addPackage(pkg)
        session = ruleBase.newStatefulSession()
        // session.addEventListener( new DebugAgendaEventListener() );
        // session.addEventListener( new DebugWorkingMemoryEventListener() );
        // logger = new WorkingMemoryFileLogger( session );
        // logger.setFileName( LOG_PATH );
    }

    void insert(Referenceable referenceable) {
        synchronized (this) {
            FactHandle factHandle = factHandles.get(referenceable.reference)
            if (factHandle) {
                session.retract(factHandle)
            }
            factHandle = session.insert(referenceable)
            factHandles.put(referenceable.reference, factHandle)
            needsFiring = true
        }
    }

    void retract(Ref ref) {
        synchronized (this) {
            FactHandle factHandle = factHandles.get(ref)
            if (factHandle) {
                session.retract(factHandle)
                factHandles.remove(ref)
                needsFiring = true
            }
            else {
                Logger.getLogger(this.class).warn("Attempted to retract not-inserted $ref")
            }
        }
    }

    void fireAllRules() {
        synchronized (this) {
            if (needsFiring) {
                session.fireAllRules()
                // logger.writeToDisk()
                needsFiring = false
            }
        }
    }

    boolean isFact(Ref ref) {
        return factHandles.get(ref) != null
    }
    

}