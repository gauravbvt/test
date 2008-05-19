package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.Channels
import org.apache.log4j.Logger
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.playbook.FlowAct
import com.mindalliance.channels.playbook.ifm.project.environment.Policy
import com.mindalliance.channels.playbook.ifm.model.TaskType
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.playbook.SharingCommitment
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.playbook.SharingAct
import com.mindalliance.channels.playbook.ifm.playbook.Detection
import com.mindalliance.channels.playbook.ifm.playbook.Causable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 9:57:33 AM
 */
class QueryManager {

    static private final boolean WARN_NO_CACHING = true

    static private QueryManager instance
    private Map cache = [:] // {queryExecution -> results}*        // TODO -- use OSCache
    private Set dirty = new HashSet() // names of "dirty" queries
    static private Map dependencies

    static QueryManager instance() {  // Singleton
        if (!instance) {
            instance = new QueryManager()
            initializeDependencies()
        }
        return instance
    }

    static void initializeDependencies() {
        dependencies = [     // large-grain dependencies but better than none
                // Channels
                findAllRelationshipNames: [Project.class, Agent.class, FlowAct.class, Policy.class],
                findAllPurposes: [Policy.class, Project.class, PlaybookModel.class, TaskType.class, SharingAgreement.class, SharingCommitment.class],
                findProjectNamed: [Channels.class],
                findUsersNotInProject: [Channels.class, Project.class],
                // Channels, Project, PlaybookModel
                findAllTypes: [PlaybookModel.class, Project.class, Channels.class],
                findAllTypesNarrowingAny: [PlaybookModel.class, Project.class, Channels.class],
                // Project
                findAllPlaceNames: [Project.class],
                atleastOnePlaceTypeDefined: [Project.class],
                findAllResourcesExcept: [Project.class],
                findAllResources: [Project.class],
                findAllResourcesOfKinds: [Project.class],
                findAllRelationshipsOf: [Project.class],
                findCandidateSubOrganizationsFor: [Project.class, Organization.class],
                findAllPositionsAnywhere: [Project.class, Organization.class],
                findAgreementsWhereSource: [Project.class, SharingAgreement.class],
                // PlaybookModel
                findInheritedTopics: [PlaybookModel.class, EventType.class],
                findNarrowedEventTypeWithTopic: [PlaybookModel.class, EventType.class],
                // Organization
                findAllPositions: [Organization.class],
                findAllSubOrganizations: [Organization.class],
                // Position
                findOtherPositionsInOrganization: [Organization.class],
                // Playbook
                findCandidateCauses: [Playbook.class, Causable.class],
                findAllEventNames: [Playbook.class],
                // Playbook, Project
                findAllAgentsExcept: [Agent.class, Project.class, Playbook.class],
                // Resource
                hasRelationship: [Project.class, Playbook.class, Agent.class, Relationship.class]
        ]
    }

    def execute(def element, Query query) {
        def results
        synchronized (this) {
            QueryExecution execution = new QueryExecution(target: element, query: query)
            results = fromCache(execution)
            if (!results) {
                results = doExecuteQuery(element, query)
                toCache(execution, results)
            }
        }
        return results
    }

    def fromCache(QueryExecution execution) {
        def results = null
        if (isCacheable(execution)) {
            cleanup(execution.query.name)
            results = cache[execution]
        }
        else {
            if (WARN_NO_CACHING) Logger.getLogger(this.class).warn("Query ${execution.query.name} is not yet cacheable (add it to dependencies)")
        }
        return results
    }

    void toCache(QueryExecution execution, def results) {
        if (isCacheable(execution)) {
            cleanup(execution.query.name)
            cache.put(execution, results)
        }
    }

    private boolean isCacheable(QueryExecution execution) {
        return dependencies.containsKey(execution.query.name)
    }

    // Query is executed in ApplicationMemory, *not* in SessionMemory
    private def doExecuteQuery(def element, Query query) {
        def results = null
        use(NoSessionCategory) {
            def target = element
            if (Ref.class.isAssignableFrom(element.class)) {
                target = element.deref()
            }
            String name = query.name
            def args = processArguments(query.arguments)
            results = target.metaClass.invokeMethod(target, name, args)
        }
        return results
    }

    Object[] processArguments(List arguments) {
        List args = []
        arguments.each {arg ->
            if (arg instanceof IModel) {
                args.add(arg.getObject())
            }
            else {
                args.add(arg)
            }
        }
        return args as Object[]
    }

    static void modified(Referenceable element) {
         instance().hasChanged(element)
    }

    private void hasChanged(Referenceable element) {     
        cache.each {qe, res ->
            List deps = dependencies[qe.query.name]
            if (deps && deps.any{dep -> dependencyMatch(dep, element)}) {
                    dirty.add(qe.query.name) // postpone cache cleanup to speed change event handling
                }
        }

 /*       if (cache) {
            dependencies.each {name, deps ->
                if (deps.any {dep -> dependencyMatch(dep, element)}) {
                    dirty.add(name) // postpone cache cleanup to speed change event handling
                }
            }
        }*/
    }

    private boolean dependencyMatch(Class dep, Referenceable element) {
        return dep.isAssignableFrom(element.class)
    }

    int size() {
        cleanupAll()
        return cache.size()
    }

    void cleanupAll() {
        List<String> all = []
        all.addAll(dirty)
        all.each {queryName -> cleanup(queryName)}
    }

    void cleanup(String queryName) {
        if (dirty && dirty.contains(queryName)) {
            List dirtyExecs = cache.keySet().findAll {qe -> qe.query.name == queryName}
            dirtyExecs.each {qe -> cache.remove(qe)}
            dirty.remove(queryName)
        }
    }

    void clear() {
        cache = [:]
        dirty = new HashSet()
    }
}