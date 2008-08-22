package com.mindalliance.channels.playbook.query

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.Project
import org.apache.log4j.Logger
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.mem.SessionMemory
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.PlaybookSession
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import com.mindalliance.channels.playbook.ifm.*
import com.mindalliance.channels.playbook.ifm.definition.*
import com.mindalliance.channels.playbook.ifm.project.environment.*
import com.mindalliance.channels.playbook.ifm.taxonomy.*
import com.mindalliance.channels.playbook.ifm.project.resources.*
import com.mindalliance.channels.playbook.ifm.playbook.*

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 9:57:33 AM
 */
class QueryManager implements PropertyChangeListener {

    static private final boolean WARN_NO_CACHING = true

    static private final boolean NO_CACHING = false

    static private QueryManager instance
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
                findProjectsForUser: [Project.class, User.class, Channels.class],
                findTaxonomiesForUser: [Taxonomy.class, User.class, Channels.class],
                findUser: [User.class],
                findProjectNamed: [Project.class],
                findUsersNotInProject: [Project.class, Participation.class, User.class],
                findAllProjectsOfUser: [Project.class, Participation.class, User.class],
                findOrganizationOfResource: [Project.class, Organization.class],
                findProjectOfElement: [Project.class, Channels.class],
                findPlaybookOfElement: [Project.class, Playbook.class],
                findTaxonomyOfElement: [Taxonomy.class, Channels.class],
                findTaxonomiesVisibleToUser: [Taxonomy.class, Project.class, Channels.class],
                // Channels, Project, Taxonomy
                findAllTypes: [Category.class],
                findAllTypesNarrowingAny: [Category.class],
                findAllPurposes: [Policy.class, TaskType.class, SharingAgreement.class, SharingCommitment.class],                
                // Project
                findProjectsOfUser: [Project.class, User.class],  // static
                findAllPlaceNames: [Place.class],
                atleastOnePlaceTypeDefined: [PlaceType.class],
                findAllResourcesExcept: [Resource.class],
                findAllResources: [Resource.class],
                findAllRelationshipsOf: [Relationship.class],
                findCandidateSubOrganizationsFor: [Organization.class],
                findAllPositionsAnywhere: [Position.class],
                findAgreementsWhereSource: [SharingAgreement.class],
                findAllPlacesOfTypeImplying: [Place.class, PlaceType.class],
                findAllPlacesInAreasOfTypeImplying: [Place.class, AreaType.class],
                findAllRelationshipNames: [Agent.class, FlowAct.class, Policy.class],
                findAllParentsOf: [Organization.class],
                findAllAgreementsBetween: [SharingAgreement.class, Agent.class],
                findAllFlowActsBetween: [Playbook.class, FlowAct.class, Agent.class],
                findAllIndividuals: [Individual.class, Organization.class],
                findAllJobsOf: [Job.class, Organization.class, Individual.class],
                // Taxonomy
                findTaxonomiesOfUser: [Taxonomy.class, User.class],  // static
                findInheritedTopics: [EventType.class],
                findNarrowedEventTypeWithTopic: [EventType.class],
                // Organization
                findAllPositions: [Position.class],
                findAllSubOrganizations: [Organization.class],
                employs: [Job.class, Position.class, Person.class, System.class, Organization.class],
                // Position
                findOtherPositionsInOrganization: [Position.class, Organization.class],
                findAllInPosition: [Position.class, Job.class],
                // Playbook
                findCandidateCauses: [Event.class],
                findPriorInformationActs: [Event.class],
                findInformationActsOfType: [InformationAct.class],
                findPriorInformationActsOfType: [Event.class],
                createsRelationshipBefore: [Event.class],
                agentImplied: [Agent.class, Association.class],
                findAllInformationActsForAgent: [InformationAct.class, Agent.class],
                findAllTopicsAboutEvent: [InformationAct.class, Event.class, EventType.class],
                findAllEventTypesFor:[InformationAct.class, Event.class, EventType.class],
                findAllPriorOccurrencesOf: [Event.class, InformationAct.class],
                // Playbook, Project
                findAllAgentsExcept: [Agent.class],
                findAllJurisdictionables: [Agent.class],
                findAllAgentsLocatedInPlacesOfTypeImplying: [Agent.class, Place.class, PlaceType.class],
                findAllAgentsWithJurisdictionsInPlacesOfTypeImplying: [Agent.class, Place.class, PlaceType.class],
                findAllAgentsLocatedInAreasOfTypeImplying: [Agent.class, AreaType.class],
                findAllAgentsWithJurisdictionsInAreasOfTypeImplying: [Agent.class, AreaType.class],
                // Resource, Group
                hasRelationship: [Resource.class, Agent.class, Relationship.class],
                // Association
                createsMatchingRelationship: [Association.class],
                // Resource
                findAllInformationActsForResource: [InformationAct.class, Resource.class],
                // Event
                findAllInformationActsCausedByEvent: [InformationAct.class, Event.class],
                findAllEventsCausedByEvent: [InformationAct.class, Event.class],
                findAllInformationActsAboutEvent: [InformationAct.class, Event.class],
                findAllPriorEvents: [Event.class],
                findAllPriorOccurrences: [Event.class, InformationAct.class],
                // Place
                findAllCandidateEnclosingPlaces: [Place.class, PlaceType.class]
        ]
    }

    QueryCache selectQueryCache(QueryExecution queryExecution) {
        QueryCache queryCache
        SessionMemory sessionMemory = PlaybookSession.current().memory
        List deps = (List) dependencies[queryExecution.query.name]
        Set<Class> deltas = sessionMemory.inSessionClasses()
        if (deps.any {clazz -> deltas.contains(clazz)}) {
            queryCache = getSessionQueryCache() // results of the query may be affected by in-session elements
        }
        else {
            queryCache = getApplicationQueryCache() // results of the query independent of elements in session
        }
        return queryCache
    }

    private QueryCache getSessionQueryCache() {
        return PlaybookSession.current().queryCache
    }

    private QueryCache getApplicationQueryCache() {
        return PlaybookApplication.current().queryCache
    }

    def execute(def element, Query query) {
        def results
        QueryExecution execution = new QueryExecution(target: element, query: query)
        results = fromCache(execution)
        if (!results) {
            results = doExecuteQuery(element, query)
            toCache(execution, results)
        }
        return results
    }

    def fromCache(QueryExecution execution) {
        Object results = null
        if (isCacheable(execution)) {
            results = selectQueryCache(execution).fromCache(execution)
        }
        else {
            if (WARN_NO_CACHING) Logger.getLogger(this.class).warn("Query ${execution.query.name} is not yet cacheable (add it to dependencies)")
        }
        return results
    }

    void toCache(QueryExecution execution, def results) {
        if (isCacheable(execution)) {
            selectQueryCache(execution).toCache(execution, results)
        }
    }

    private boolean isCacheable(QueryExecution execution) {
        return !NO_CACHING && dependencies.containsKey(execution.query.name)
    }

    // Query is executed in SessionMemory, results will be cached in ApplicationMemory if not dependent on elements currently in session
    private def doExecuteQuery(def element, Query query) {
        def results
        def target = element
        if (Ref.class.isAssignableFrom(element.class)) {
            target = element.deref()
        }
        String name = query.name
        def args = processArguments(query.arguments)
        results = target.metaClass.invokeMethod(target, name, args)
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

    static void modifiedInApplication(Referenceable element) {
        QueryCache queryCache = instance().getApplicationQueryCache()
        instance().hasChanged(element, queryCache)
    }

    void propertyChange(PropertyChangeEvent evt) { // only elements in session can be modified and thus raise change events
        Referenceable element = (Referenceable) evt.source
        QueryCache queryCache = getSessionQueryCache()
        hasChanged(element, queryCache)
    }

    private void hasChanged(Referenceable element, QueryCache cache) {
        cache.cachedExecutions.each {qe ->
            String queryName = qe.query.name
            List deps = (List) dependencies[queryName]
            if (deps && deps.any {dep -> dependencyMatch(dep, element)}) {
                cache.cleanup(queryName)
            }
        }
    }

    private boolean dependencyMatch(Class dep, Referenceable element) {
        return dep.isAssignableFrom(element.class)
    }

    // For testing
    int sessionCacheSize() {
        return getSessionQueryCache().size()
    }

    int applicationCacheSize() {
        return getApplicationQueryCache().size()
    }

}