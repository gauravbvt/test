package com.mindalliance.channels.metamodel.beans.environment

import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.metamodel.beans.AbstractModelerBean
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 19, 2008
* Time: 11:24:40 AM
* To change this template use File | Settings | File Templates.
*/
class Environment extends AbstractModelerBean {

    public static final String ID = "ENVIRONMENT"

    def name = new SimpleData(dataClass: String.class)
    def description = new SimpleData(dataClass: String.class)
    BeanList persons = new BeanList(itemPrototype: new BeanReference(beanClass: Person.class.name), itemName: 'person')
    BeanList organizations = new BeanList(itemPrototype: new BeanReference(beanClass: Organization.class.name), itemName: 'organization')
    BeanList clearances = new BeanList(itemPrototype: new BeanReference(beanClass: Clearance.class.name), itemName: 'clearance')
    BeanList policies = new BeanList(itemPrototype: new BeanReference(beanClass: Policy.class.name), itemName: 'policy')
    BeanList positions = new BeanList(calculate: 'calculate_positions', itemName: 'position')
    BeanList systems = new BeanList(calculate: 'calculate_systems', itemName: 'system')

    public Map getBeanProperties() {
        return [name:name, description:description, persons: persons, organizations: organizations, positions: positions, systems: systems, clearances: clearances, policies: policies]
    }

    void initialize() {
        defaultMetadata = [
            name: [required: true],
            description: [required: true]
        ]
    }

    def calculate_positions() {
        List allPositions = []
        this.organizations.collect {org -> allPositions.addAll(org.positions)}
        return allPositions
    }

    def calculate_systems() {
        List allSystems = []
        this.organizations.collect {org -> allSystems.addAll(org.systems)}
        return allSystems
    }

}