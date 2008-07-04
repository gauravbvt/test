package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 3:39:39 PM
*/
class AreaType extends ElementType {

    static Ref globe() {
        return ComputedRef.from(AreaType.class, "makeGlobe")
    }

    static Ref continent() {
        return ComputedRef.from(AreaType.class, "makeContinent")
    }

    static Ref country() {
        return ComputedRef.from(AreaType.class, "makeCountry")
    }

    static Ref state() {
        return ComputedRef.from(AreaType.class, "makeState")
    }

    static Ref county() {
        return ComputedRef.from(AreaType.class, "makeCounty")
    }

    static Ref city() {
        return ComputedRef.from(AreaType.class, "makeCity")
    }

    static AreaType makeGlobe() {
        return new AreaType(name: 'Globe')
    }

    static AreaType makeContinent() {
        AreaType continent = new AreaType(name: 'Continent')
        continent.narrow(AreaType.global())
        return continent
    }

    static AreaType makeCountry() {
        AreaType country = new AreaType(name: 'Country')
        country.narrow(AreaType.continent())
        return country
    }

    static AreaType makeState() {
        AreaType state = new AreaType(name: 'State')
        state.narrow(AreaType.country())
        return state
    }

    static AreaType makeCounty() {
        AreaType county = new AreaType(name: 'County')
        county.narrow(AreaType.state())
        return county
    }

    static AreaType makeCity() {
        AreaType city = new AreaType(name: 'City')
        city.narrow(AreaType.county())
        return city
    }

    static List<String> allAreaTypeNames() {
        return [country().name, state().name, county().name, city().name]
    }

    static Ref areaTypeNamed(String name) {
        switch(name) {
            case 'Country': return country()
            case 'State': return state()
            case 'County': return county()
            case 'City': return city()
            default: null
        }
    }
 }