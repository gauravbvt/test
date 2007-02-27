// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.model;

import com.mindalliance.channels.util.GUID;


/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class TestModelObject extends AbstractModelObject {

    private String name;
    private int age;
    
    /**
     * @param guid
     */
    TestModelObject( GUID guid ) {
        super( guid );
    }

    public int getAge() {
        return age;
    }
    
    public void setAge( int age ) {
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
}
