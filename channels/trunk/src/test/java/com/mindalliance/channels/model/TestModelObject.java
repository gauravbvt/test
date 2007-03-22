// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class TestModelObject extends ModelElement {

    private String name;
    private int age;
    
    /**
     * @param guid
     */
    TestModelObject() {
        super();
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
