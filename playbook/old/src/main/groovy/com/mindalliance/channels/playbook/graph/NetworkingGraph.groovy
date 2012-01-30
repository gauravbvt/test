package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ifm.playbook.Group
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.models.Container
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
            netEdge: [dir: 'none', fontname: PlaybookGraph.LABEL_FONT_NAME, fontsize: PlaybookGraph.LABEL_FONT_SIZE]
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
            if (ref as boolean) {
                Referenceable el = ref.deref()
                switch (el) {
                    case Resource.class: processResource((Resource) el); break
                }
            }
        }
    }

    void processResource(Ref res) {
        if (res as boolean) processResource((Resource)res.deref())
    }

    void processResource(Resource resource) {
        resources.add(resource.reference)
        // if (resource.isOrganizationResource()) resources.add(resource.organization)
    }

    void buildResources(GraphVizBuilder builder) {
        resources.each {ref ->
            Resource res = (Resource)ref.deref()
            builder.node(name: nameFor(res), label: labelFor(res), URL: urlFor(res), template: 'resource')
        }
    }

    void buildNetworking(GraphVizBuilder builder) {
        List<Ref> list = resources as List
        for (int i=0; i< list.size()-1; i++ ) {
            for (int j=i+1; j < list.size(); j++) {
                Ref ref = (Ref)list[i]
                Ref otherRef = (Ref)list[j]
                Networking networking = new Networking(resource: ref, otherResource: otherRef)
                    if (networking.size() > 0) {
                        builder.edge(source: nameFor(ref), target: nameFor(otherRef), label: "${networking.size()}", URL: urlFor(networking), template: "netEdge")
                    }
            }
        }
    }

    protected String nameFor(Networking networking) {
       return "${nameFor(networking.resource)}_${nameFor(networking.otherResource)}"
    }


    protected String labelFor(Networking networking) {
       return "${networking.size()}"
    }

    protected String urlFor(Networking networking) {
       return urlFor(networking.reference)
    }

}