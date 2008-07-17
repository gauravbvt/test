package com.mindalliance.channels.playbook.report

import groovy.xml.MarkupBuilder
import com.mindalliance.channels.playbook.ifm.Tab
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.model.Role
import com.mindalliance.channels.playbook.ifm.project.resources.Team
import com.mindalliance.channels.playbook.ifm.project.resources.Position
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.System
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.resources.Person
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.playbook.FlowAct

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2008
 * Time: 2:55:19 PM
 */
class ResourceDirectory extends Report {

    static final boolean EXPAND = true
    Set processed = new HashSet()

    ResourceDirectory(Tab tab) {
        super(tab)
    }

    String getTitle() {
        return "Resource Directory";
    }
    /*
    resource =
        element resource {
            attribute type {text} // people, organizations, systems, positions...
            attribute id {text}
            element name {text},
            element description {text}?,
            element organization {
                attribute name {text},
                attribute id {text}
            }?
            element role  {
                attribute name {text},
                attribute id {text}
            }*,
            element contactInfo {
                attribute medium {text},
                text // end point
            }*,
            element instructions {text}?,
            group?
        }
    group =
        element group {
            attribute type {text},
            attribute name {text},
            attribute id {text}?,
            group*,
            resource*
        }
    element body {
        group*,
        resource*
    }
     */

    void buildBody(MarkupBuilder xml) {
        List elements = []
        this.tab.iterator().each {ref ->
            if (ref as boolean) elements.add(ref.deref())
        }
        sortOnNames(elements).each {el -> processElement(el, xml)}
    }

    private void processElement(Referenceable element, MarkupBuilder xml) {
        switch (element) {
            case Project.class: processProject((Project) element, xml); break
            case Playbook.class: processPlaybook((Playbook) element, xml); break
            case Role.class: processRole((Role) element, xml); break
            case Location.class: processLocation((Location) element, xml); break
            case Resource.class: processResource((Resource) element, EXPAND, xml); break
            case InformationAct.class: processInformationAct((InformationAct) element, xml)
        }
    }

    private void processProject(Project project, MarkupBuilder xml) {
        if (processed.contains(project.reference)) return
        xml.group(type: project.type, name: "In project ${project.name}") {
            project.findAllResources().each {res ->
                processResource((Resource) res.deref(), !EXPAND, xml)
            }
        }
        processed.add(project.reference)
    }

    private void processPlaybook(Playbook playbook, MarkupBuilder xml) {
        if (processed.contains(playbook.reference)) return
        xml.group(type: playbook.type, name: "In playbook ${playbook.name}") {
            playbook.findAllAgents().each {ref ->
                Referenceable agent = ref.deref()
                if (agent instanceof Resource) {
                    processResource((Resource) agent, !EXPAND, xml)
                }
            }
        }
        processed.add(playbook.reference)
    }

    private void processInformationAct(InformationAct act, MarkupBuilder xml) {
        if (processed.contains(act.reference)) return
        Ref ref = act.actorAgent
        if (ref as boolean) {
            Agent actor = (Agent) ref.deref()

            if (!processed.contains(actor.reference) && actor instanceof Resource) processResource((Resource) actor, !EXPAND, xml)
        }
        if (act instanceof FlowAct) {
            ref = ((FlowAct) act).targetAgent
            if (ref as boolean) {
                Agent target = (Agent) ref.deref()
                if (!processed.contains(target.reference) && target instanceof Resource) processResource((Resource) target, !EXPAND, xml)
            }
        }
        processed.add(act.reference)
    }

    private void processRole(Role role, MarkupBuilder xml) {
        if (processed.contains(role.reference)) return
        xml.group(type: role.type, name: "In role ${role.name}", id: role.id) {
            this.userProjects.each {project ->
                project.findAllResources().each {ref ->
                    Resource res = (Resource) ref.deref()
                    boolean roleImplied = res.roles.any {it.implies(role.reference)}
                    if (roleImplied) {
                        processResource((Resource) res, !EXPAND, xml)
                    }
                }
            }
        }
        processed.add(role.reference)
    }

    private void processLocation(Location loc, MarkupBuilder xml) {
        if (processed.contains(loc.reference)) return
        xml.group(type: 'Location', name: "In location $loc") {
            this.userProjects.each {project ->
                project.findAllResources().each {ref ->
                    Resource res = (Resource) ref.deref()
                    if (res.hasLocation() && res.location.isWithin(loc) ||
                            res.hasJurisdiction() && res.jurisdiction.isWithin(loc)) {
                        processResource((Resource) res, !EXPAND, xml)
                    }
                }
            }
        }
        processed.add(loc.reference)
    }

    private void processResource(Resource res, boolean expand, MarkupBuilder xml) {
        xml.resource(type: res.type, id: res.id) {
            name(res.name)
            description(res.description)
            if (res.isOrganizationResource()) {
                xml.organization(name: res.organization.name, id: res.organization.id)
            }
            res.roles.each {role -> xml.role(name: role.name, id: role.id)}
            xml.contactInfos {
                if (!res.contactInfos) {
                    xml.noContactInfo()
                }
                else {
                    res.contactInfos.each {contactInfo ->
                        xml.contactInfo(medium: contactInfo.mediumType.name, contactInfo.endPoint)
                    }
                }
            }
            if (res instanceof System) processSystem((System) res, xml)
            switch (res) {
                case Organization.class: if (expand) processOrganization((Organization) res, xml); break
                case Position.class: processPosition((Position) res, xml); break
                case Team.class: if (expand) processTeam((Team) res, xml)
            }
        }
        processed.add(res.reference)
    }

    private void processOrganization(Organization org, MarkupBuilder xml) {
        if (processed.contains(org.reference)) return
        List<Ref> resources = org.resources
        if (resources) {
            xml.group(type: org.type, name: 'Resources in this organization', id: org.id) {
                sortOnNames(resources).each {res ->
                    processResource((Resource) res.deref(), !EXPAND, xml)
                }
            }
        }
        processed.add(org.reference)
    }

    private void processPosition(Position pos, MarkupBuilder xml) {
        List<Ref> persons = (List<Ref>) Query.execute(pos, "findAllPersonsInPosition")
        if (persons) {
            xml.group(type: pos.type, name: 'Persons in this position', id: pos.id) {
                sortOnNames(persons).each {person ->
                    processResource((Resource) person.deref(), !EXPAND, xml)
                }
            }
        }
        processed.add(pos.reference)
    }

    private void processTeam(Team team, MarkupBuilder xml) {
        if (processed.contains(team.reference)) return
        List<Ref> members = team.resources
        if (members) {
            xml.group(type: team.type, name: 'Team members', id: team.id) {
                sortOnNames(members).each {member ->
                    processResource((Resource) member.deref(), !EXPAND, xml)
                }
            }
        }
        processed.add(team.reference)
    }

    private void processSystem(System system, MarkupBuilder xml) {
        if (system.instructions) {
            xml.instruction(system.instructions)
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