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
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.playbook.FlowAct
import com.mindalliance.channels.playbook.ifm.project.resources.OrganizationResource
import com.mindalliance.channels.playbook.ifm.info.GeoLocation
import com.mindalliance.channels.playbook.ifm.info.AreaInfo

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

    ResourceDirectory(Tab tab) {
        super(tab)
    }

    String getTitle() {
        return "Resource Directory";
    }

    void buildBody(MarkupBuilder xml) {
        Set<Referenceable> elements = new HashSet()
        this.tab.iterator().each {ref ->
            if (ref as boolean) elements.add(ref.deref())
        }
        elements.each {el -> extractResources(el)}
        buildDirectory(xml)
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
        retain(act.actorAgent)
        if (act instanceof FlowAct) {
            retain(act.targetAgent)
        }
    }

    private void extractResourcesFromRole(Role role) {
        this.userProjects.each {project ->
            project.findAllResources().each {ref ->
                Resource res = (Resource) ref.deref()
                boolean roleImplied = res.roles.any {it.implies(role.reference)}
                if (roleImplied) {
                    retain(res.reference)
                }
            }
        }
    }

    private void extractResource(Resource res) {
        retain(res.reference)
    }

    private void retain(Ref ref) {
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
    }

    private void addPosition(Position position) {
        List<Ref> allInPosition = (List<Ref>) Query.execute(position, "findAllInPosition")
        allInPosition.each {res ->
            retain(res)
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

    private void buildDirectory(MarkupBuilder xml) {
        Map<Ref, String> firsts = buildIndex(xml)
        buildEntries(firsts, xml)
    }

    // Map<String, Set<Ref>> index = new HashMap<String, Set<Ref>>()
    private Map<Ref, String> buildIndex(MarkupBuilder xml) {
        Map<Ref, String> firsts = new HashMap<Ref, String>()
        xml.index {
            (('A'..'Z') + ['*']).each {key ->
                List<Ref> refs = sortOnNames((index[key] ?: []) as List<Ref>)
                if (refs) {
                    xml.entry(key: key, ref: refs[0].id)
                    firsts[refs[0]] = key
                }
            }
        }
        return firsts
    }

    private void buildEntries(Map<Ref, String> firsts, MarkupBuilder xml) {
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
                        List<Ref> members = sortOnNames(orgs[ref] as List<Ref>)
                        if (members) {
                            xml.members {
                                members.each {member ->
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
            res.jobs.each {position ->
                if (position as boolean) {
                    retain(position)
                    xml.job(ref: position.id, position.name)
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
            res.findAllRoles().each {role ->
                if (role as boolean) {
                    xml.role(role.name)
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
        List<Ref> projects = (List<Ref>) Query.execute(Channels.instance(), "findAllProjectsOfUser", this.user)
        return projects
    }

}