package com.mindalliance.channels.playbook.report

import groovy.xml.MarkupBuilder
import com.mindalliance.channels.playbook.ifm.Tab
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.taxonomy.Role
import com.mindalliance.channels.playbook.ifm.project.resources.Position
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.System
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.resources.Person
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.playbook.FlowAct
import com.mindalliance.channels.playbook.ifm.project.resources.OrganizationResource
import com.mindalliance.channels.playbook.ifm.info.GeoLocation
import com.mindalliance.channels.playbook.ifm.info.AreaInfo
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2008
 * Time: 2:55:19 PM
 */
class ResourceDirectory extends Report {

    Map<String, Set<Ref>> index = new HashMap<String, Set<Ref>>()
    Map<Ref, Set<Ref>> orgs = new HashMap<Ref, Set<Ref>>()
    Set<Ref> retained = new HashSet<Ref>()
    Map<Ref, String> firsts = new HashMap<Ref, String>()

    ResourceDirectory(Tab tab) {
        super(tab)
    }

    String getTitle() {
        return "Resource Directory";
    }

    // Map<String, Set<Ref>> index = new HashMap<String, Set<Ref>>()
    protected void buildIndex(MarkupBuilder xml) {
        xml.index {
            firsts.each {ref, key ->
                xml.entry(key: key, ref: ref.id)
            }
        }
    }

    protected void buildBody(MarkupBuilder xml) {
        Set<Referenceable> elements = new HashSet()
        this.tab.iterator().each {ref ->
            if (ref as boolean) elements.add(ref.deref())
        }
        elements.each {el -> extractResources(el)}
        computeIndex()
        buildResources(xml)
    }

    private void computeIndex() {
        (('A'..'Z') + ['*']).each {key ->
            List<Ref> refs = sortOnNames((index[key] ?: []) as List<Ref>)
            if (refs) {
                firsts[refs[0]] = key
            }
        }
    }

    private void extractResources(Referenceable res) {
        switch (res) {
            case Project.class: extractResourcesFromProject((Project) res); break
            case Playbook.class: extractResourcesFromPlaybook((Playbook) res); break
            case InformationAct.class: extractResourcesFromInformationAct((InformationAct) res); break
            case Role.class: extractResourcesFromRole((Role) res); break
            case Resource.class: extractResource((Resource) res)
        // default = ignore
        }
    }

    private void extractResourcesFromProject(Project project) {
        project.findAllResources().each {retain(it)}
    }

    private void extractResourcesFromPlaybook(Playbook playbook) {
        playbook.findAllAgents().each {retain(it)}
    }

    private void extractResourcesFromInformationAct(InformationAct act) {
        if (act as boolean) {
            act.actors.each { retain(it) }
            if (act instanceof FlowAct) {
                retain(act.targetAgent)
            }
        }
    }

    private void extractResourcesFromRole(Role role) {
        this.userProjects.each {project ->
            if (project as boolean) {
                project.findAllResources().each {ref ->
                    Resource res = (Resource) ref.deref()
                    if (res as boolean && res.isAgent()) {
                        boolean roleImplied = res.roles.any {it.implies(role.reference)}
                        if (roleImplied) {
                            retain(res.reference)
                        }
                    }
                }
            }
        }
    }

    private void extractResource(Resource res) {
        retain(res.reference)
    }

    private void retain(Ref ref) {
        if (ref as boolean) {
            if (!retained.contains(ref)) {
                retained.add(ref)
                Resource res = (Resource) ref.deref()
                switch (res) {
                    case System.class:
                        addToOrganization((OrganizationResource) res); break
                    case Position.class:
                        addToOrganization((OrganizationResource) res)
                        addPosition((Position) res); break
                    case Organization.class:
                        addToIndex(res); break
                    case Person.class:
                        addToIndex(res)
                }
                addJobs(res)
            }
        }
    }

    private void addPosition(Position position) {
        List<Ref> allInPosition = (List<Ref>) Query.execute(position, "findAllInPosition")
        allInPosition.each {res ->
            retain(res)
        }
    }

    private void addJobs(Resource res) {
        if (res as boolean) {
            if (res.isAnIndividual()) {
                res.jobs.each {job ->
                    if (job as boolean && job.position as boolean) {
                        retain(job.position)
                    }
                }
            }
        }
    }

    private void addToIndex(Resource res) {
        String name = res.name
        String key = (name && (name[0].toUpperCase() in ('A'..'Z'))) ? name[0] : '*'
        Set<Ref> indexed = (Set<Ref>) index[key]
        if (indexed == null) {
            indexed = new HashSet<Ref>()
            index.put(key, indexed)
        }
        indexed.add(res.reference)
    }

    private void addToOrganization(OrganizationResource orgRes) {
        Ref org = orgRes.organization
        if (org as boolean) {
            Set<Ref> orgResources = (Set<Ref>) orgs.get(org.reference)
            if (orgResources == null) {
                orgResources = new HashSet<Ref>()
                orgs.put(org, orgResources)
                addToIndex((Resource) org.deref()) // add the organization to the index
            }
            orgResources.add(orgRes.reference)
        }
    }


    private void buildResources(MarkupBuilder xml) {
        (('A'..'Z') + ['*']).each {key ->
            List<Ref> refs = sortOnNames((index[key] ?: []) as List<Ref>)
            refs.each {ref ->
                Resource res = (Resource) ref.deref()
                Map attributes = [id: ref.id, type: res.type]
                if (firsts[ref]) {
                    attributes += [first: firsts[ref]]
                }
                xml.resource(attributes) {  // either organization or person
                    // name, location, jurisdiction, roles, contact info
                    buildResourceCard(res, xml)
                    // if organization, add included org resources
                    if (res instanceof Organization) {
                        Set<Ref> members = (Set<Ref>) orgs[ref]
                        if (members) {
                            xml.members {
                                sortOnNames(members as List<Ref>).each {member ->
                                    if (member as boolean) {
                                        xml.resource(id: member.id, type: member.type) {
                                            buildResourceCard((Resource) member.deref(), xml)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildResourceCard(Resource res, MarkupBuilder xml) {
        xml.name(res.name)
        buildResourceAddress(res, xml)
        buildRoles(res, xml)
        if (res instanceof Position) {
            buildPositionHolders((Position) res, xml)
        }
        buildJobs(res, xml)
        buildContactInfos(res, xml)
    }

    private void buildResourceAddress(Resource res, MarkupBuilder xml) {
        GeoLocation geoLoc = res.location.effectiveGeoLocation
        if (geoLoc.isDefined()) {
            AreaInfo areaInfo = geoLoc.areaInfo
            if (areaInfo.isDefined()) {
                xml.address {
                    xml.street(areaInfo.street)
                    xml.city(areaInfo.city)
                    xml.state(areaInfo.state)
                    xml.country(areaInfo.country)
                    xml.code(areaInfo.code)
                }
            }
        }
    }

    private void buildJobs(Resource res, MarkupBuilder xml) {
        xml.jobs {
            if (res.isAnIndividual()) {
                res.jobs.each {job ->
                    if (job as boolean && job.position as boolean) {
                        xml.job(ref: job.position.id, job.position.name)
                    }
                }
            }
        }
    }

    private void buildPositionHolders(Position position, MarkupBuilder xml) {
        xml.holders {
            position.findAllInPosition().each {res ->
                xml.holder(ref: res.id, res.name)
            }
        }
    }

    private void buildRoles(Resource res, MarkupBuilder xml) {
        xml.roles {
            if (res.isAgent()) {
                res.findAllRoles().each {role ->
                    if (role as boolean) {
                        xml.role(role.name)
                    }
                }
            }
        }
    }

    private void buildContactInfos(Resource res, MarkupBuilder xml) {
        xml.contactInfos {
            res.contactInfos.each {ci ->
                if (ci.mediumType as boolean) {
                    xml.contactInfo(medium: ci.mediumType.name, ci.endPoint)
                }
            }
        }
    }

    private List sortOnNames(List named) {
        return named.sort {ref1, ref2 ->
            nameOf(ref1).compareTo(nameOf(ref2))
        }
    }

    private String nameOf(Ref ref) {
        return nameOf(ref.deref())
    }

    private String nameOf(Referenceable el) {
        switch (el) {
            case Person: return el.lastName + el.firstName + el.middleName
            case Named: return el.name
            default: return el.toString()
        }
    }

    private List<Ref> getUserProjects() {
        return RefUtils.getUserProjects()
    }

}