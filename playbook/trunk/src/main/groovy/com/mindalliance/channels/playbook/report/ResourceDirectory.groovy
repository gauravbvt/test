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

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2008
 * Time: 2:55:19 PM
 */
class ResourceDirectory extends Report {

    static final boolean EXPAND = true

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
            element name {text},
            element description {text}?,
            element organization {text}?
            element role {text}*,
            element contactInfo {
                attribute medium {text},
                text // end point
            }*,
            element instructions {text}?,
            group?
        }*
    group =
        element group {
            attribute type {text},
            attribute name {text},
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
            case Resource.class: processResource((Resource) element, EXPAND, xml)
        }
    }

    private void processProject(Project project, MarkupBuilder xml) {
        // TODO
    }

    private void processPlaybook(Playbook playbook, MarkupBuilder xml) {
        // TODO
    }

    private void processRole(Role role, MarkupBuilder xml) {
        // TODO
    }

    private void processLocation(Location loc, MarkupBuilder xml) {
        // TODO
    }

    private void processResource(Resource res, boolean expand, MarkupBuilder xml) {
        xml.resource(type: res.type) {
            name(res.name)
            description(res.description)
            if (res.isOrganizationResource()) {
                xml.organization(res.organization.name)
            }
            res.roles.each {role -> xml.role(role.name)}
            res.contactInfos.each {contactInfo ->
                xml.contactInfo(medium: contactInfo.mediumType.name) {
                    contactInfo.endPoint
                }
            }
            if (expand) {
                switch (res) {
                    case Organization.class: processOrganization((Organization) res, xml); break
                    case Position.class: processPosition((Position) res, xml); break
                    case Team.class: processTeam((Team) res, xml); break
                    case System.class: processSystem((System) res, xml)
                }
            }
        }
    }

    private void processOrganization(Organization org, MarkupBuilder xml) {
        List<Ref> resources = org.resources
        if (resources) {
            xml.group(type: org.type, name: 'Resources in this organization') {
                sortOnNames(resources).each {res ->
                     processResource((Resource) res.deref(), !EXPAND, xml)
                 }
            }
        }
    }

    private void processPosition(Position pos, MarkupBuilder xml) {
        List<Ref> persons = (List<Ref>) Query.execute(pos, "findAllPersonsInPosition")
        if (persons) {
            xml.group(type: pos.type, name: 'Persons in this position') {
                sortOnNames(persons).each {person ->
                    processResource((Resource) person.deref(), !EXPAND, xml)
                }
            }
        }
    }

    private void processTeam(Team team, MarkupBuilder xml) {
        List<Ref> members = team.resources
        if (members) {
            xml.group(type: team.type, name: 'Team members') {
                sortOnNames(members).each {member ->
                    processResource((Resource) member.deref(), !EXPAND, xml)
                }
            }
        }
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
        Referenceable el = ref.deref()
        switch (el) {
            case Person: return  el.lastName + el.firstName + el.middleName
            case Named: return el.name
            default: return ''
        }
    }

}