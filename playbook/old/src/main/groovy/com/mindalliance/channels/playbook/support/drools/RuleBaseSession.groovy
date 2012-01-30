package com.mindalliance.channels.playbook.support.drools
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
import com.mindalliance.channels.playbook.ref.Ref
import org.drools.FactHandle
import com.mindalliance.channels.playbook.ref.Referenceable
import org.drools.RuleBaseConfiguration
import org.drools.QueryResults
import org.drools.QueryResult
import com.mindalliance.channels.playbook.support.PlaybookApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RuleBaseSession implements Serializable {

    static Logger L4jLogger = LoggerFactory.getLogger(RuleBaseSession.class)
    static final List<String> FLOW = ['validations', 'profiles', 'completeness', 'beliefs', 'compliance', 'issues', 'actions']

    private String rulesPackageName
    private StatefulSession session
    private LoggerAdapter logger
    private Map<Ref, FactHandle> factHandles
    private Set<Referenceable> inserts
    private Set<Ref> retracts

    RuleBaseSession(String rulesPackageName) {
        this.rulesPackageName = rulesPackageName
        initialize()
    }

    static RuleBaseSession current() {
        return PlaybookApplication.current().ruleBaseSession
    }

    static List query(String name, List arguments, String binding) {
        return current().executeQuery(name, arguments, binding)
    }

    private void reset() {
        inserts = Collections.synchronizedSet(new HashSet<Referenceable>())
        retracts = Collections.synchronizedSet(new HashSet<Ref>())
    }

    private void initialize() {
        Package pkg = PackageLoader.loadPackage(this.class, rulesPackageName)
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.shadowProxy = false;
        RuleBase ruleBase = RuleBaseFactory.newRuleBase(conf)
        ruleBase.addPackage(pkg)
        session = ruleBase.newStatefulSession()
        reset()

        logger = new LoggerAdapter( L4jLogger, session );
        factHandles = new HashMap<Ref, FactHandle>()
    }

    void clear() {
        session = null
    }

    void insert(Referenceable referenceable) {
        assert !referenceable.reference.isInferred()
        inserts.add(referenceable)
    }

    void retract(Ref ref) {
        assert !ref.isInferred()
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
            l4jLogger.warn("Attempted to retract not-inserted $ref")
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

    Referenceable deref(String id) {
        List results = doExecuteQuery("deref", [id], "element")
        if (results) {
            assert results.size() == 1
            Referenceable element = (Referenceable)results[0]
            return element
        }
        else {
            return null
        }
    }

    List<Object> executeQuery(String name, List arguments, String binding) {
        List<Object> answers = doExecuteQuery(name, arguments, binding)
        return answers
    }

    private List<Object> doExecuteQuery(String name, List arguments, String binding) {
        List<Object> answers = []
        Object[] args = arguments as Object[]
        QueryResults results = session.getQueryResults(name, args)
        for (Iterator iter = results.iterator();  iter.hasNext();) {
            QueryResult result = (QueryResult)iter.next()
            answers.add(result.get(binding))
        }
        return answers
    }


}