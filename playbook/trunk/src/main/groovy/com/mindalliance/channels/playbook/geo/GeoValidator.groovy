package com.mindalliance.channels.playbook.geo

import org.apache.wicket.validation.validator.AbstractValidator
import org.apache.wicket.validation.IValidatable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 24, 2008
* Time: 8:19:19 PM
*/
class GeoValidator extends AbstractValidator {

    public static final List<String> COUNTRY = ['PCLI']
    public static final List<String> STATE = ['ADM1']
    public static final List<String> COUNTY = ['ADM2']
    public static final List<String> CITY = ['PPL', 'PPLA', 'PPLC']

    List<String> featureCodes
    String type

    static GeoValidator exists(List<String> codes) {
        GeoValidator geoValidator = new GeoValidator()
        geoValidator.featureCodes = codes
        switch (codes) {
            case COUNTRY: geoValidator.type = 'country'; break
            case STATE: geoValidator.type = 'state'; break
            case COUNTY: geoValidator.type = 'county'; break
            case CITY: geoValidator.type = 'city'; break
            default: geoValidator.type = "$codes"
        }
        return geoValidator
    }

    static GeoValidator countryExists() {
        return exists(COUNTRY)
    }

    static GeoValidator stateExists() {
        return exists(STATE)
    }

    static GeoValidator cityExists() {
        return exists(CITY)
    }

    static GeoValidator country(String name) {
        return GeoValidator.featureCode(COUNTRY)
    }

    static GeoValidator state(String name) {
        return GeoValidator.featureCode(STATE)
    }

    static GeoValidator county(String name) {
        return GeoValidator.featureCode(COUNTY)
    }

    static GeoValidator city(String name) {
        return GeoValidator.featureCode(CITY)
    }

    protected Map variablesMap(IValidatable validatable)
    {
        final Map map = super.variablesMap(validatable);
        map.put("type", type);
        return map;
    }

    /**
     * @see AbstractValidator#resourceKey(FormComponent)
     */
    protected String resourceKey()
    {
        return "GeoValidator.exists";
    }

    protected void onValidate(IValidatable validatable) {
        String name = (String)validatable.getValue()
        boolean known
        try {
            known = GeoService.exists(name, featureCodes)
        }
        catch (Exception e) {
            warning("Could not verify $name because geo service failed")
            known = true
        }
        if (!known) {
            error(validatable)
        }
    }

}