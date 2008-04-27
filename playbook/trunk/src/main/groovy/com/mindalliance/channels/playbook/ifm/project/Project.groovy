package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.resources.Resource
import com.mindalliance.channels.playbook.ifm.resources.Position
import com.mindalliance.channels.playbook.ifm.resources.Organization
import com.mindalliance.channels.playbook.ifm.resources.System
import com.mindalliance.channels.playbook.ifm.resources.Person
import com.mindalliance.channels.playbook.support.PlaybookSession
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ifm.environment.Environment
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.model.Model
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 2:10:46 PM
 */
class Project extends IfmElement {

    String name = 'Unnamed'
    String description = ''
    List<Ref> participations = []
    List<Ref> resources = []
    List<Ref> playbooks = []
    List<Ref> models = []
    List<Ref> environments = []
    List<Ref> analysisElements = []

    static Ref current() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.project
    }

    String toString() { name }

    Referenceable doAddToField( String field, Object object ) {
        switch ( object ) {
            case Participation: super.doAddToField( "participations", object ); break;
            case Position:
            case Person:
            case System:
            case Organization:  super.doAddToField( "resources", object ); break;
            case Playbook:  super.doAddToField( "playbooks", object ); break;
            case Model:  super.doAddToField( "models", object ); break;
            case Environment: super.doAddToField( "environments", object ); break;
            default: super.doAddToField( field, object );
        }
    }

    Referenceable doRemoveFromField( String field, Object object ) {
        switch ( object ) {
            case Participation: super.doRemoveFromField( "participations", object ); break;
            case Position:
            case Person:
            case System:
            case Organization:  super.doRemoveFromField( "resources", object ); break;
            case Playbook:  super.doRemoveFromField( "playbooks", object ); break;
            case Model:  super.doRemoveFromField( "models", object ); break;
            case Environment: super.doRemoveFromField( "environments", object ); break;
            default: super.doRemoveFromField( field, object );
        }
    }

    Ref findResourceNamed(String type, String name) {
        Ref res = (Ref) resources.find {res ->
            res.type == type && res.name.equalsIgnoreCase(name)
        }
        return res
    }

    Ref findAResource(String type) {
        Ref res = (Ref)resources.find {res ->
            res.type == type
        }
        return res
    }

    Ref findPlaybookNamed(String type, String name) {
        Ref sc = (Ref) playbooks.find {sc ->
            sc.type == type && sc.name.equalsIgnoreCase(name)
        }
        return res
    }

    List<Ref> findAllResourcesOfType(String type) {
        return (List<Ref>) resources.findAll {res -> res.type == type}
    }

    Ref findParticipation(Ref user) {
        Ref p = (Ref) participations.find {p -> p.user == user }
        return p
    }


    List<String> findAllPlaceNames() {
        List<String> names = []
        environments.each {env ->
            env.places.each {place ->
                names.add(place.name)
            }
        }
        return names
    }

    Ref findPlaceNamed(String placeName) {
        Ref namedPlace
        environments.any {env ->
            env.places.any {place ->
                if (place.name == placeName) { namedPlace = place }
                namedPlace
            }
            namedPlace
        }
        return namedPlace
    }

    boolean atleastOnePlaceTypeDefined() {
        Ref model = (Ref) models.find {model ->
            model.placeTypes.size() > 0
        }
        return model != null
    }

    List<Ref> findAllTypes(String typeType) {
        List<Ref> types = []
        String propName = RefUtils.decapitalize("${typeType}s")
        models.each {model ->
            types.addAll(model."$propName")
        }
        return types
    }

    Ref findElementTypeNamed(String typeType, String elementTypeName) {
        String propName = RefUtils.decapitalize("${typeType}s")
        Ref namedType = null
        models.any {model ->
            List list = model."$propName"
            list.any {type ->
                String typeName = type.name
                if (typeName.equalsIgnoreCase(elementTypeName)) {
                    namedType = type
                }
                namedType
            }
            namedType
        }
        return namedType
    }

    List<Ref> findAllTypesNarrowing(Ref elementType) {
        List<Ref> types = []
        findAllTypes(elementType.type).each {type ->
            if (type.narrows(elementType)) types.add(type)
        }
        return types
    }

    List<Ref> findPlaceTypesNarrowing(Ref placeType) {
        List<Ref> narrowing = []
        models.each {model ->
            model.placeTypes.each {pt ->
                if (placeType == null && pt.parent == null) { // top level place types narrow undefined place type
                    narrowing.add(pt)
                }
                else if (pt.parent == placeType) {
                    narrowing.add(pt)
                }
            }
        }
        return narrowing
    }

    Boolean isParticipant( Ref user ) {
         return findParticipation( user ) != null ;
     }

     Boolean isManager( Ref user ) {
         Ref ref = findParticipation(user)
         return ref != null && ref.manager ;
     }

     /**
      * Return project contents that a participant can add.
      */
     static List<Class<?>> contentClasses() {
         List<Class<?>> result = new ArrayList<Class<?>>()
         result.addAll( Resource.contentClasses() )
         result.addAll( [ Environment.class ] )
         result.addAll( Playbook.contentClasses() )
         return result
     }

     void addContents( List<Ref> result ) {
         playbooks.each { it.addContents( result ) }
         result.addAll( resources )
         result.addAll( environments )
         result.addAll( analysisElements )
     }

     void addManagerContents( List<Ref> result ) {
         result.addAll( playbooks )
     }

     /**
      * Return system objects that a project manager can add.
      */
     static List<Class<?>> managerClasses() {
         [ Project.class, Playbook.class ]
     }


}