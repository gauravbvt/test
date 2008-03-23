package com.mindalliance.channels.playbook.geo

import com.mindalliance.channels.playbook.ifm.Location
import org.geonames.Toponym
import org.geonames.WebService
import org.geonames.ToponymSearchCriteria
import org.geonames.FeatureClass
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.HttpStatus
import groovy.util.slurpersupport.GPathResult
import org.geonames.Style

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 22, 2008
* Time: 11:15:38 AM
*/
class GeoService {

    static final List<String> CITY = ['PPL', 'PPLA', 'PPLC']
    static final String COUNTY = 'ADM2'
    static final String STATE = 'ADM1'

    static Map<Location,Area> areas = Collections.synchronizedMap(new HashMap<Location,Area>())

    // Find the area identified by the location
    static Area locate(Location location) {
        assert location.country
        Area area = areas[location]
        if (!area) {
            Toponym topo
            ToponymSearchCriteria tsc = searchCriteriaFrom(location)
            List<Toponym> topos = WebService.search(tsc).toponyms
            if (topos.size() == 1) {  // successful, unambiguous search
               area = new Area(topos[0])
               areas[location] = area
            }
        }
        return area
    }

    static private ToponymSearchCriteria searchCriteriaFrom(Location location) {
       ToponymSearchCriteria tsc = new ToponymSearchCriteria()
       tsc.style = Style.FULL
       // Set search country code
       Area country = findCountry(location.country)
       assert country
       Area state
       tsc.countryCode = country.countryCode
       String name
       String featureClass
        if (location.state) {
             name = location.state
             tsc.featureCode = STATE
             state = findState(country, location.state)
             assert state
             tsc.adminCode1 = state.adminCode1
             if (location.county) {
                 name = location.county
                 tsc.featureCode = COUNTY
             }
            if (location.city) {
                name = location.city
                tsc.featureCode = CITY[0] // TODO -- Does this cover all? What about PPLA and PPLC?
            }
         }
       if (!name) throw new IllegalArgumentException("Incomplete location $location")
       tsc.nameEquals = name
       return tsc
    }

    // Return nearby areas of the same kind
    static List<Area> findNearbyAreas(Area area) {
       // http://ws.geonames.org/findNearby?lat=48.865618158309374&lng=2.344207763671875&featureClass=P&featureCode=PPLA&featureCode=PPL&featureCode=PPLC
       String url = WebService.geonamesServer
       url += '/findNearby?'
       url += "lat=${area.latitude}&lng=${area.longitude}"
       if (area.isCityLike()) {
           url += "&featureCode=PPLA&featureCode=PPL&featureCode=PPLC"
       }
       else {
           url += "&featureCode=${area.featureCode}"
       }
       List<Area> list = doRestCall(url)
       return list
    }

    static List<Area> findHierarchy(Area area) {
        // http://ws.geonames.org/hierarchy?geonameId=2657896
        assert area.geonameId
        String url = WebService.geonamesServer
        url += "/hierarchy?geonameId=${area.geonameId}"
        List<Area> list = doRestCall(url)
        // Remove last which is area
        if (list.size() > 0) list.remove(list.size() - 1)
        return list.reverse()
    }

    static Area findCountry(String name) {
        Area area
        Location location = new Location(country: name)
        area = areas[location]
        if (!area) {
            ToponymSearchCriteria tsc = new ToponymSearchCriteria()
            tsc.nameEquals = name
            tsc.featureClass = FeatureClass.A
            List<Toponym>topos = WebService.search(tsc).toponyms
            Toponym countryTopo = (Toponym)topos.find {topo ->
                topo.countryCode && topo.featureCode =="PCLI"
            }
            if (countryTopo) {
                area = new Area(countryTopo)
                areas[location] = area
            }
        }
        return area
    }

    static Area findState(Area country, String name) {
        Area area
        Location location = new Location(country: country.countryName, state: name)
        area = areas[location]
        if (!area) {
            ToponymSearchCriteria tsc = new ToponymSearchCriteria()
            tsc.style = Style.FULL
            tsc.nameEquals = name
            tsc.featureCode = 'ADM1'
            List<Toponym>topos = WebService.search(tsc).toponyms
            Toponym stateTopo = (Toponym)topos.find {topo ->
                topo.featureCode =="ADM1"
            }
            if (stateTopo) {
                area = new Area(stateTopo)
                areas[location] = area
            }
        }
        return area
    }

    static List<Area> doRestCall(String url) {
        List<Area> list = []
        HttpClient client = new HttpClient()
        GetMethod get = new GetMethod(url)
        get.setRequestHeader("Accept", "text/xml")
        try {
            int statusCode = client.executeMethod(get)
            if (statusCode != HttpStatus.SC_OK) {
              System.err.println("Method failed: " + get.getStatusLine())  // TODO --- log this
              throw new Exception("Geoname call failed: ${get.getStatusLine()}")
            }
            String xml = get.getResponseBodyAsString().toString()
            list = parseResults(xml)
            }
        finally {
            get.releaseConnection()
        }
        return list
    }

    static private List<Area> parseResults(String xml) {
        List<Area> list = []
        GPathResult res = new XmlSlurper().parseText(xml)
        res.geoname.each() {geo ->
            Toponym topo = new Toponym()
            topo.name = geo.name.text()
            topo.latitude = Double.parseDouble(geo.lat.text())
            topo.longitude = Double.parseDouble(geo.lng.text())
            def gid = geo.geonameId
            if (gid) topo.geonameId = Integer.parseInt(gid.text())
            topo.countryCode = geo.countryCode.text()
            topo.countryName = geo.countryName.text()
            topo.featureClass = FeatureClass.fromValue(geo.fcl.text())
            topo.featureClassName = geo.fclName.text()
            topo.featureCode = geo.fcode.text()
            topo.featureCodeName = geo.fCodeName.text()
            def population = geo.population
            if (population.text().size()) topo.population = Integer.parseInt(population.text())
            def elevation = geo.elevation
            if (elevation.text().size()) topo.elevation = Integer.parseInt(elevation.text())
            list.add(new Area(topo))
        }
        return list
    }
}