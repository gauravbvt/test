package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ifm.playbook.Group
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.graph.support.Networking

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 28, 2008
 * Time: 3:41:52 PM
 */
class NetworkingGraph extends PlaybookGraph {

    Set<Ref> resources = new HashSet<Ref>()

    NetworkingGraph(Container container) {
        super(container)
    }

    Map getStyleTemplate() {
        return super.getStyleTemplate() + [
            networking: [shape: 'circle', fillcolor: 'azure2', fontname: PlaybookGraph.LABEL_FONT_NAME, fontsize:PlaybookGraph.LABEL_FONT_SIZE],
            fromEdge: [dir: 'none']
        ]
    }

    List<Ref> allElements() {
        return resources as List<Ref>
    }

    void buildContent(GraphVizBuilder builder) {
        processData()
        buildResources(builder)
        buildNetworking(builder)
        super.buildContent(builder)
    }

    void processData() {
        container.iterator().each {ref ->
            if (el as boolean) {
                Referenceable el = ref.deref()
                switch (el) {
                    case Resource.class: processResource((Resource) el); break
                    case InformationAct.class: processInformationAct((InformationAct) el); break
                    case Group.class: processGroup((Group) el); break
                    case Playbook.class: processPlaybook((Playbook) el); break
                    case SharingAgreement.class: processSharingAgreement((SharingAgreement)agreement); break
                    case Relationship.class: processRelationship((Relationship)relationship); break
                    case Project.class: processProject((Project) el); break
                }
            }
        }
    }

    void processResource(Ref res) {
        if (res as boolean) processResource((Resource)res.deref())
    }

    void processResource(Resource resource) {
        resources.add(resource.reference)
        if (resource.isOrganizationResource()) resources.add(resource.organization)
    }

    void processGroup(Group group) {
        Ref latest = group.playbook.latestOccurrence
        group.getResourcesAt(latest).each {processResource(it)}
    }

    void processInformationAct(InformationAct act) {
        if (act as boolean) {
            Ref latest = playbook.latestOccurrence
            if (act.actorAgent as boolean) {
                act.actorAgent.getResourcesAt(latest).each {processResource(it)}
            }
            if (act.isFlowAct() && act.targetAgent as boolean) {
                act.targetAgent.getResourcesAt(latest).each {processResource(it)}
            }
        }
    }

    void processSharingAgreement(SharingAgreement sharingAgreement) {
        if (sharingAgreement.source) processResource(sharingAgreement.source)
        if (sharingAgreement.recipient) processResource(sharingAgreement.recipient)
    }

    void processRelationship(Relationship relationship) {
        if (relationship.fromAgent as boolean) processResource(relationship.fromAgent)
        if (relationship.toAgent as boolean) processResource(relationship.toAgent)
    }

    void processPlaybook(Playbook playbook) {
        playbook.informationActs.each {act ->
            if (act as boolean) processInformationAct((InformationAct)act.deref())
        }
    }

    void processProject(Project project) {
        project.resources.each {processResource(it)}
    }

    void buildResources(GraphVizBuilder builder) {
        resources.each {ref ->
            Resource res = (Resource)ref.deref()
            builder.node(name: nameFor(res), label: labelFor(res), URL: urlFor(res), template: 'agent')
        }
    }

    void buildNetworking(GraphVizBuilder builder) {
        resources.each {fromRef ->
            resources.each {toRef ->
                if (fromRef != toRef) {
                    Networking networking = new Networking(fromResource: fromRef, toResource: toAgent)
                    if (networking.size() > 0) {
                        builder.node(name: nameFor(networking), label: labelFor(networking), URL, urlFor(networking), template: 'networking')
                        builder.edge(source: nameFor(fromRef), target: nameFor(networking), template: 'fromEdge')
                        builder.edge(source: nameFor(networking), target:nameFor(toRef))
                    }
                }
            }
        }
    }

    protected String nameFor(Networking networking) {
       return "${nameFor(networking.fromResource)}_${nameFor(networking.toResource)}"
    }


    protected String labelFor(Networking networking) {
       return "${networking.size()}"
    }

    protected String urlFor(Networking networking) {
       return urlFor(networking.reference)
    }

}