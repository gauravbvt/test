package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Person extends Resource {
    String firstName
    String middleName
    String lastName
    Address address
    URL photo

//    List<ContactInfo> contactInfos = new ArrayList<ContactInfo>()
//    List<Position> positions = new ArrayList<Position>()

    Person( String name ) {
        super( name )
    }

    String getType() {
        return "Person";
    }
}