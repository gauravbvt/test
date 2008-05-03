package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.model.Model
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.project.environment.Policy
import com.mindalliance.channels.playbook.ifm.project.environment.Place
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.Person
import com.mindalliance.channels.playbook.ifm.project.resources.Position
import com.mindalliance.channels.playbook.ifm.project.resources.System

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.PlaybookSession
import com.mindalliance.channels.playbook.support.RefUtils
import org.apache.wicket.Session

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 2:10:46 PM
 */
class Project extends IfmElement implements Describable {

    String name = 'Unnamed'
    String description = ''
    List<Ref> participations = []
    List<Ref> resources = []
    List<Ref> places = []
    List<Ref> policies = []
    List<Ref> agreements = []
    List<Ref> playbooks = []
    List<Ref> models = []
    List<Ref> analysisElements = []

    static Ref current() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.project
    }

    String toString() { name }

    Referenceable doAddToField( String field, Object object ) {
        object.project = this.reference
        switch ( object.deref() ) {
            case Participation: super.doAddToField( "participations", object ); break;
            case Position:
            case Person:
            case System:
            case Organization:  super.doAddToField( "resources", object ); break;
            case Playbook:  super.doAddToField( "playbooks", object ); break;
            case Model:  super.doAddToField( "models", object ); break;
            case Place: super.doAddToField( "places", object ); break;
            case Policy: super.doAddToField( "policies", object ); break;
            default: super.doAddToField( field, object );
        }
    }

    Referenceable doRemoveFromField( String field, Object object ) {
        switch ( object.deref() ) {
            case Participation: super.doRemoveFromField( "participations", object ); break;
            case Position:
            case Person:
            case System:
            case Organization:  super.doRemoveFromField( "resources", object ); break;
            case Playbook:  super.doRemoveFromField( "playbooks", object ); break;
            case Model:  super.doRemoveFromField( "models", object ); break;
            case Place: super.doRemoveFromField( "places", object ); break;
            case Policy: super.doRemoveFromField( "policies", object ); break;
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

    List<Ref> allResourcesExcept(Ref resource) {
        return resources.findAll {res -> res != resource }
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
        return places.collect {it.name}
    }

    Ref findPlaceNamed(String placeName) {
        Ref namedPlace = (Ref)places.find {place -> place.name == placeName }
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
        models.each {model ->
            types.addAll(model.findAllTypes(typeType))
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

    List<Ref> findAllTypesNarrowingAny(List<Ref> elementTypes) {
       Set<Ref> types = new HashSet<Ref>()
       elementTypes.each {elementType ->
           types.addAll(this.findAllTypesNarrowing(elementType))
       }
        return types as List
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

    List<Ref> findAllApplicableRelationshipTypes(Ref fromResource, Ref toResource) {
        List<Ref> relTypes = []
        models.each { model ->
            relTypes.addAll(
                    model.relationshipTypes.findAll {rt ->
                        rt.matchesFrom(fromResource) && rt.matchesTo(toResource)
            })
        }
        return relTypes
    }

    List<Ref> findAllAgreementsOf(Ref resource) {
        List<Ref> ags = agreements.findAll {agreement ->
            agreement.fromResource == resource
        }
        return ags ?: []
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
         // When changing this method, don't forget to update the next one...
         List<Class<?>> result = new ArrayList<Class<?>>()
         result.addAll( Resource.contentClasses() )
         result.addAll( [ Policy.class ] )
         result.addAll( [ Place.class ] )
         result.addAll( [ Playbook.class ] )
         result.addAll( Playbook.contentClasses() )
         return result
     }

     void addContents( List<Ref> result ) {
         playbooks.each { it.addContents( result ) }
         result.addAll( resources )
         result.addAll( policies )
         result.addAll( places )
         result.addAll( playbooks )
         result.addAll( analysisElements )
         playbooks.each { it.addContents( result ) }
     }

     /**
      * Return system objects that a project manager can add.
      */
     static List<Class<?>> managerClasses() {
         [ Project.class ]
     }

     void addManagerContents( List<Ref> result ) {
        // Projects are added in UserScope.getContents()
     }

}