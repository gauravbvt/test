package com.beanview.test;

import java.util.Collection;

import com.beanview.BeanView;
import com.beanview.annotation.PropertyOptions;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:59 $
 */

public class SinglePersonPicker
{
    SimpleObject allPeople;

    SimpleObject favoriteLastNameMPeople;

    SimpleObject peopleByContext;

    SimpleObject peopleByObjectMethod;

    SimpleObject peopleLetterMByObjectMethod;

    SimpleObject peopleByObjectMethodWithContext;

    /** Testing retrieval by static factory method with no context */
    @PropertyOptions(options = "SimpleObjectFactory.getLastNameStartsWithM")
    public SimpleObject getFavoriteLastNameMPeople()
    {
        return favoriteLastNameMPeople;
    }

    public void setFavoriteLastNameMPeople(SimpleObject favoriteLastNameMPeople)
    {
        this.favoriteLastNameMPeople = favoriteLastNameMPeople;
    }

    /** Testing retrieval by static factory method with no context */
    @PropertyOptions(options = "SimpleObjectFactory.getPotentialObjects")
    public SimpleObject getAllPeople()
    {
        return allPeople;
    }

    public void setAllPeople(SimpleObject allPeople)
    {
        this.allPeople = allPeople;
    }

    /** Testing retrieval by static factory method with a context */
    @PropertyOptions(options = "SimpleObjectFactory.getFromUserIDContext")
    public SimpleObject getPeopleByContext()
    {
        return peopleByContext;
    }

    public void setPeopleByContext(SimpleObject peopleByContext)
    {
        this.peopleByContext = peopleByContext;
    }

    /** Testing retrieval by a local object method without a context */
    @PropertyOptions(options = "findAllPeople")
    public SimpleObject getPeopleByObjectMethod()
    {
        return peopleByObjectMethod;
    }

    public void setPeopleByObjectMethod(SimpleObject peopleByObjectMethod)
    {
        this.peopleByObjectMethod = peopleByObjectMethod;
    }

    /** Testing retrieval by a local object method without a context */
    @PropertyOptions(options = "findPeopleByContext")
    public SimpleObject getPeopleByObjectMethodWithContext()
    {
        return peopleByObjectMethodWithContext;
    }

    public void setPeopleByObjectMethodWithContext(
            SimpleObject peopleByObjectMethodWithContext)
    {
        this.peopleByObjectMethodWithContext = peopleByObjectMethodWithContext;
    }

    /** Testing retrieval by a local object method without a context */
    @PropertyOptions(options = "findAllLetterMPeople")
    public SimpleObject getPeopleLetterMByObjectMethod()
    {
        return peopleLetterMByObjectMethod;
    }

    public void setPeopleLetterMByObjectMethod(
            SimpleObject peopleLetterMByObjectMethod)
    {
        this.peopleLetterMByObjectMethod = peopleLetterMByObjectMethod;
    }

    public Collection<SimpleObject> findAllPeople()
    {
        return SimpleObjectFactory.getPotentialObjects();
    }

    public Collection<SimpleObject> findAllLetterMPeople()
    {
        return SimpleObjectFactory.getLastNameStartsWithM();
    }

    public Collection<SimpleObject> findPeopleByContext(BeanView context)
    {
        return SimpleObjectFactory.getFromUserIDContext(context);
    }
}
