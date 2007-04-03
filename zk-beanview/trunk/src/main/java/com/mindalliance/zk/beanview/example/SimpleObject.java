package com.mindalliance.zk.beanview.example;

/**
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class SimpleObject
{
    static int identifier = 0;

    String ID;

    String firstName = "First";

    String lastName = "Last";

    public SimpleObject()
    {
        ID = Integer.toString(identifier++);
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getID()
    {
        return this.ID;
    }

    public void setID(String id)
    {
        this.ID = id;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String toString()
    {
        return lastName + ", " + firstName;
    }
}
