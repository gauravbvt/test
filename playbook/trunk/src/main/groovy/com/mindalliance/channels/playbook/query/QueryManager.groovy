package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.Ref
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.Channels
import org.apache.log4j.Logger
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 9:57:33 AM
 */
class QueryManager implements PropertyChangeListener {

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
        dependencies = [
                // Channels, Project, PlaybookModel
                findAllTypes: [[PlaybookModel.class],[Project.class, 'models'], [Channels.class, 'models']],
                findAllTypesNarrowingAny: [[PlaybookModel.class],[Project.class, 'models'], [Channels.class, 'models']],
                findAllPlaceNames: [[Project.class, 'places']],
                // Project
                atleastOnePlaceTypeDefined: [[Project.class, 'places']],
                allResourcesExcept: [[Project.class]],
                findAllApplicableRelationshipTypes: [[PlaybookModel.class, 'relationshipTypes']],
                findCandidateSubOrganizationsFor: [[Project.class, 'organizations'], [Organization.class, 'subOrganizations', 'parent']],
                findAllPositionsAnywhere: [[Project.class, 'organizations'], [Organization.class, 'positions']],
                // Channels
                findProjectNamed: [[Channels.class, 'projects']],
                findUsersNotInProject: [[Channels.class, 'users'], [Project.class, 'participations']],
                // PlaybookModel
                findInheritedTopics: [[PlaybookModel.class, 'eventTypes'], [EventType.class, 'topics', 'narrowedTypes']],
                findNarrowedEventTypeWithTopic: [[PlaybookModel.class, 'eventTypes'], [EventType.class, 'topics', 'narrowedTypes']],
                // Organization
                findAllPositions: [[Organization.class, 'positions', 'parent']],
                findAllSubOrganizations: [[Organization.class, 'subOrganizations','parent']],
                // Position
                findOtherPositionsInOrganization: [[Organization.class, 'positions']]
        ]
    }

    def execute(def element, Query query) {
        QueryExecution execution = new QueryExecution(target: element, query: query)
        def results = fromCache(execution)
        if (!results) {
            results = executeQuery(element, query)
            toCache(execution, results)
        }
        return results
    }

    def fromCache(QueryExecution execution) {
        def results = null
        if (isCacheable(execution)) {
            clear(execution.query.name)
            results = cache[execution]
        }
        else {
            if (WARN_NO_CACHING) Logger.getLogger(this.class).warn("Query ${execution.query.name} is not yet cacheable (add it to dependencies)")
        }
        return results
    }

    void toCache(QueryExecution execution, def results) {
        if (isCacheable(execution)) {
            clear(execution.query.name)
            cache.put(execution, results)
        }
    }

    private boolean isCacheable(QueryExecution execution) {
       return dependencies.containsKey(execution.query.name)
    }

    def executeQuery(def element, Query query) {
        def target = element
        if (Ref.class.isAssignableFrom(element.class)) {
            target = element.deref()
        }
        String name = query.name
        def args = processArguments(query.arguments)
        def results = target.metaClass.invokeMethod(target, name, args)
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

    void propertyChange(PropertyChangeEvent evt) {
        Referenceable referenceable = (Referenceable) evt.source
        String propertyName = evt.getPropertyName()
        hasChanged(referenceable, propertyName)
    }

    private void hasChanged(Referenceable element, String propName) {
        if (cache) {
            dependencies.each {name, deps ->
                if (deps.any{dep -> dependencyMatch(dep, element, propName)}) {
                    dirty.add(name) // postpone cache cleanup to speed change event handling
                }
            }
        }
    }

    private boolean dependencyMatch(List dep, Referenceable element, String propName) {
        boolean dependent = false
        if (dep[0].isAssignableFrom(element.class)) {
            if (dep.size() > 1) {
                dependent = dep[1..dep.size()-1].any {propName == it}
            }
            else {
                dependent = true
            }
        }
        return dependent
    }

    int size() {
        return cache.size()
    }

    void clear(String queryName) {
        if (dirty && dirty.contains(queryName)) {
           List dirtyExecs = cache.keySet().findAll {qe-> qe.query.name == queryName}
           dirtyExecs.each {qe -> cache.remove(qe)}
           dirty.remove(queryName)
        }
    }

    void clear() {
        cache = [:]
        dirty = new HashSet()
    }
}