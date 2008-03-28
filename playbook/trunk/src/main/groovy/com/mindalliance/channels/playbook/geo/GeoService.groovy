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

    static final String COUNTRY = 'PCLI'
    static final List<String> CITY = ['PPL', 'PPLA', 'PPLC']
    static final String COUNTY = 'ADM2'
    static final String STATE = 'ADM1'

    static final String SEARCH = 'search'
    static final String CODE = 'code'

    static Map<Location, Area> areas = Collections.synchronizedMap(new HashMap<Location, Area>())

    // Find the area identified by the location
    static Area locate(Location location) {
        assert location.country
        Area area = areas[location]
        if (!area) {
            Toponym topo
            ToponymSearchCriteria tsc = searchCriteriaFrom(location)
            List<Toponym> topos = WebService.search(tsc).toponyms
            if (topos.size() == 1) {// successful, unambiguous search
                area = new Area(topos[0])
                areas[location] = area
            }
            else {
                if (topos.size() == 0) {
                    throw new UnknownAreaException("No known area for location $location")
                }
                else {// > 1
                    throw new AmbiguousAreaException("${topos.size()} areas found for location $location", topos)
                }
            }
        }
        return area
    }

    static private ToponymSearchCriteria searchCriteriaFrom(Location location) {
        String name
        ToponymSearchCriteria tsc = new ToponymSearchCriteria()
        tsc.style = Style.FULL
        if (location.country) {
            name = location.country
            // Set search country code
            Area country = findCountry(name)
            if (!country) {throw new UnknownAreaException("Unknown country $name")}
            Area state
            tsc.countryCode = country.countryCode
            String featureClass
            if (location.state) {
                name = location.state
                tsc.featureCode = STATE
                state = findState(country, name)
                if (!state) {throw new UnknownAreaException("Unknown state $name")}
                tsc.adminCode1 = state.adminCode1
                if (location.county) {
                    name = location.county
                    tsc.featureCode = COUNTY
                    Area county = findCounty(country, state, name)
                    if (!county) {throw new UnknownAreaException("Unknown county $name")}
                    tsc.adminCode2 = county.adminCode2
                }
                if (location.city) {
                    name = location.city
                    tsc.featureCodes = CITY as String[]
                }
            }
        }
        if (!name) throw new AreaException("Incomplete location $location")
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
            List<Toponym> topos
            try {
                topos = WebService.search(tsc).toponyms
            } catch (Exception e) {
                throw new ServiceFailureAreaException("Failed to find country $name", e)
            }
            Toponym countryTopo = (Toponym) topos.find {topo ->
                topo.countryCode && topo.featureCode == "PCLI"
            }
            if (countryTopo) {
                area = new Area(countryTopo)
                areas[location] = area
            }
            else {
                throw new UnknownAreaException("No such country $name")
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
            tsc.countryCode = country.topo.countryCode
            tsc.nameEquals = name
            tsc.featureCode = 'ADM1'
            List<Toponym> topos
            try {
                topos = WebService.search(tsc).toponyms
            } catch (Exception e) {
                throw new ServiceFailureAreaException("Failed to find state $name", e)
            }
            Toponym stateTopo = (Toponym) topos.find {topo ->
                topo.featureCode == "ADM1"
            }
            if (stateTopo) {
                area = new Area(stateTopo)
                areas[location] = area
            }
            else {
                throw new UnknownAreaException("No such state $name")
            }
        }
        return area
    }

    static Area findCounty(Area country, Area state, String name) {
        Area area
        Location location = new Location(country: country.countryName, state: state.name, county:name)
        area = areas[location]
        if (!area) {
            ToponymSearchCriteria tsc = new ToponymSearchCriteria()
            tsc.style = Style.FULL
            tsc.countryCode = country.topo.countryCode
            tsc.adminCode1 = state.topo.adminCode1
            tsc.nameEquals = name
            tsc.featureCode = 'ADM2'
            List<Toponym> topos
            try {
                topos = WebService.search(tsc).toponyms
            } catch (Exception e) {
                throw new ServiceFailureAreaException("Failed to find county $name", e)
            }
            Toponym stateTopo = (Toponym) topos.find {topo ->
                topo.featureCode == "ADM2"
            }
            if (stateTopo) {
                area = new Area(stateTopo)
                areas[location] = area
            }
            else {
                throw new UnknownAreaException("No such county $name")
            }
        }
        return area
    }

    static boolean exists(String name, List<String> featureCodes) throws Exception {
        ToponymSearchCriteria tsc = new ToponymSearchCriteria()
        tsc.name = name
        tsc.featureCodes = featureCodes as String[]
        List<Toponym> topos
        try {
            topos = WebService.search(tsc).toponyms
        } catch (Exception e) {
            System.err.println("Geonames search failed: $e") // TODO - log this
            throw e
        }
        boolean exists = !topos.isEmpty()
        return exists
    }

    static List<String> findCandidateCountryNames(String input, int max) {
        ToponymSearchCriteria tsc = new ToponymSearchCriteria()
        tsc.nameStartsWith = input
        tsc.featureCode = COUNTRY
        tsc.maxRows = max
        tsc.style = Style.SHORT
        List<String> names = []
        try {
            List<Toponym> topos = WebService.search(tsc).toponyms
            names = topos.collect {topo -> topo.name}
        }
        catch (Exception e) {
            System.err.println("Failed to look up candidate country names: $e")
        }
        return names.sort()
    }

    static List<String> findCandidateStateNames(String input, String countryName, int max) {
        List<String> names = []
        try {
            Area country = findCountry(countryName)
            ToponymSearchCriteria tsc = new ToponymSearchCriteria()
            tsc.nameStartsWith = input
            tsc.featureCode = STATE
            tsc.countryCode = country.topo.countryCode
            tsc.maxRows = max
            tsc.style = Style.SHORT
            List<Toponym> topos = WebService.search(tsc).toponyms
            names = topos.collect {topo -> topo.name}
        }
        catch (Exception e) {
            System.err.println("Failed to look up candidate state names: $e")
        }
        return names.sort()
    }

    static List<String> findCandidateCountyNames(String input, String countryName, String stateName, int max)  {
        List<String> names = []
        try {
            Area country = findCountry(countryName)
            Area state = findState(country, stateName)
            ToponymSearchCriteria tsc = new ToponymSearchCriteria()
            tsc.nameStartsWith = input
            tsc.featureCodes = COUNTY as String[]
            tsc.countryCode = country.topo.countryCode
            tsc.adminCode1 = state.topo.adminCode1
            tsc.maxRows = max
            tsc.style = Style.SHORT
            List<Toponym> topos = WebService.search(tsc).toponyms
            names = topos.collect {topo -> topo.name}
        }
        catch (Exception e) {
            System.err.println("Failed to look up candidate county names: $e")
        }
        return names.sort()
    }

    static List<String> findCandidateCityNames(String input, String countryName, String stateName, String countyName, int max) {
        List<String> names = []
        try {
            Area country = findCountry(countryName)
            Area state = findState(country, stateName)
            Area county
            if (countyName) county = findCounty(country, state, countyName)
            ToponymSearchCriteria tsc = new ToponymSearchCriteria()
            tsc.nameStartsWith = input
            tsc.featureCodes = CITY as String[]
            tsc.countryCode = country.topo.countryCode
            tsc.adminCode1 = state.topo.adminCode1
            if (county) tsc.adminCode2 = county.topo.adminCode2
            tsc.maxRows = max
            tsc.style = Style.SHORT
            List<Toponym> topos = WebService.search(tsc).toponyms
            names = topos.collect {topo -> topo.name}
        }
        catch (Exception e) {
            System.err.println("Failed to look up candidate city names: $e")
        }
        return names.sort()
    }

    static boolean validateCode(String code, String countryName, String stateName, String countyName, String cityName) {
        try {
            assert code
            String encodedCode = URLEncoder.encode(code)
            Area country
            if (countryName) country= findCountry(countryName)
            Area state
            if (stateName) state = findState(country, stateName)
            Area county
            if (countyName) county = findCounty(country, state, countyName)
            String url = WebService.geonamesServer
            url += "/postalCodeSearch?postalcode=$encodedCode&maxRows=1"
            if (countryName) url += "&country=${country.topo.countryCode}"
            List<Area> list = doRestCall(url, CODE)
            if (list) {
               Area codeArea = list[0]
                if (country) {
                  if (codeArea.topo.countryCode != country.topo.countryCode) return false
                 }
               if (state) {
                if (codeArea.topo.adminCode1 != state.topo.adminCode1) return false
               }
                if (county) {
                 if (codeArea.topo.adminCode2 != county.topo.adminCode2) return false
                }
               if (cityName) {
                   if (!cityName.equalsIgnoreCase(codeArea.topo.name)) return false
               }
               return true
            }
        } catch (AreaException e) {
            System.err.println("Failed to validate code: $e")
        }
        return false
    }

    // SUPPORT

    private static List<Area> doRestCall(String url) {
        return doRestCall(url, SEARCH)
    }

    private static List<Area> doRestCall(String url, String resultType) {
        List<Area> list = []
        HttpClient client = new HttpClient()
        GetMethod get = new GetMethod(url)
        get.setRequestHeader("Accept", "text/xml")
        try {
            int statusCode = client.executeMethod(get)
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + get.getStatusLine()) // TODO --- log this
                throw new ServiceFailureAreaException("Geoname $url call failed: ${get.getStatusLine()}")
            }
            String xml = get.getResponseBodyAsString().toString()
            if (resultType == SEARCH) {
                list = parseSearchResults(xml)
            }
            else if (resultType == CODE) {
                list = parseCodeResults(xml)
            }
            else {
                throw new IllegalArgumentException("Unsupported result type")
            }
        }
        catch (Exception e) {
            throw new ServiceFailureAreaException("Geoname $url call failed", e)
        }
        finally {
            get.releaseConnection()
        }
        return list
    }

    static private List<Area> parseSearchResults(String xml) {
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

    static private List<Area> parseCodeResults(String xml) {
        List<Area> list = []
        GPathResult res = new XmlSlurper().parseText(xml)
        res.code.each() {code ->
            Toponym topo = new Toponym()
            topo.name = code.name.text()
            topo.latitude = Double.parseDouble(code.lat.text())
            topo.longitude = Double.parseDouble(code.lng.text())
            topo.countryCode = code.countryCode.text()
            topo.adminCode1 = code.adminCode1.text()
            topo.adminCode2 = code.adminCode2.text()
            list.add(new Area(topo))
        }
        return list
    }

}