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
import com.mindalliance.channels.playbook.ref.Ref
import org.drools.FactHandle
import com.mindalliance.channels.playbook.ref.Referenceable
import org.apache.log4j.Logger
import org.drools.RuleBaseConfiguration;


class RuleBaseSession implements Serializable {

    static final String LOG_PATH = "rules_log/firing"
    static final boolean LOG_FIRING = false

    static final List<String> FLOW = ['validations', 'profiles', 'beliefs', 'cases', 'issues', 'actions']

    private String rulesPackageName
    private StatefulSession session
    private WorkingMemoryFileLogger logger
    private Map<Ref, FactHandle> factHandles
    private Set<Referenceable> inserts
    private Set<Ref> retracts

    RuleBaseSession(String rulesPackageName) {
        this.rulesPackageName = rulesPackageName
        initialize()
    }

    private void reset() {
        factHandles = Collections.synchronizedMap(new HashMap<Ref, FactHandle>())
        inserts = Collections.synchronizedSet(new HashSet<Referenceable>())
    }

    private void initialize() {
        Package pkg = PackageLoader.loadPackage(this.class, rulesPackageName)
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setShadowProxy(false);
        RuleBase ruleBase = RuleBaseFactory.newRuleBase(conf)
        ruleBase.addPackage(pkg)
        session = ruleBase.newStatefulSession()
        reset()
        if (LOG_FIRING) {
            session.addEventListener(new DebugAgendaEventListener());
            session.addEventListener(new DebugWorkingMemoryEventListener());
            logger = new WorkingMemoryFileLogger(session);
            logger.setFileName(LOG_PATH);
        }
    }

    void insert(Referenceable referenceable) {
        inserts.add(referenceable)
    }

    void retract(Ref ref) {
        retracts.add(ref)
    }

    void doInsert(Referenceable referenceable) {
        FactHandle factHandle = factHandles.get(referenceable.reference)
        if (factHandle) {
            session.retract(factHandle)
        }
        factHandle = session.insert(referenceable)
        factHandles.put(referenceable.reference, factHandle)
    }

    void doRetract(Ref ref) {
        FactHandle factHandle = factHandles.get(ref)
        if (factHandle) {
            session.retract(factHandle)
            factHandles.remove(ref)
        }
        else {
            Logger.getLogger(this.class).warn("Attempted to retract not-inserted $ref")
        }
    }

    void fireAllRules() {
        synchronized (this) {
            if (inserts || retracts) {
                inserts.each {doInsert(it)}
                retracts.each {doRetract(it)}
                reset()
                setFlow(FLOW)
                session.fireAllRules()
                if (LOG_FIRING) logger.writeToDisk()
            }
        }
    }

    private void setFlow(List<String> flow) {
        flow.reverse().each {session.setFocus((String)it)}
    }

    boolean isFact(Ref ref) {
        boolean isFact
        synchronized (this) {
            isFact = factHandles.get(ref) != null || inserts.any {it.reference == ref}
        }
        return isFact
    }


}