// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import com.mindalliance.mindpeer.model.Product;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.List;

/**
 * ...
 */
public class TestProductPage extends IntegrationTest {

    private ProductPage page;

    private User user;

    private Profile profile;

    @Before
    public void init() {
        super.init();
    }

    private void loadPage() {
        login( "guest", "" );
        tester.startPage( ProductPage.class );
        tester.assertRenderedPage( ProductPage.class );
        page = (ProductPage) tester.getLastRenderedPage();
        user = page.getUser();
        profile = user.getProfile();
    }

    @After
    public void after() {
        logout();
    }

    @Test
    @Transactional
    @Rollback
    public void newProduct() throws URISyntaxException {
        loadPage();
        assertEquals( 0, profile.getProductCount() );
        Label productCount = (Label) tester.getComponentFromLastRenderedPage( "profile.productCount" );
        assertEquals( "0", productCount.getDefaultModelObjectAsString() );

        tester.clickLink( "newItem" );
        tester.assertRenderedPage( ProductPage.class );
        assertEquals( 1, profile.getProductCount() );
        Page renderedPage = tester.getLastRenderedPage();
        Component component = renderedPage.get( "tabs:name" );
        assertEquals( "feed", component.getDefaultModelObjectAsString() );
        PageParameters parms = renderedPage.getPageParameters();
        assertEquals( "feed", parms.getString( "name" ) );
        assertEquals( "Comments", parms.getString( "section" ) );

        tester.clickLink( "newItem" );
        tester.assertRenderedPage( ProductPage.class );
        assertEquals( 2, profile.getProductCount() );
        Page page2 = tester.getLastRenderedPage();
        assertEquals( "2", tester.getComponentFromLastRenderedPage( "profile.productCount" ).getDefaultModelObjectAsString() );
        assertEquals( "feed2", page2.getPageParameters().getString( "name" ) );
    }

    @Test
    @Transactional
    @Rollback
    public void clickTabs() throws URISyntaxException {
        loadPage();
        tester.clickLink( "newItem" );
        tester.clickLink( "newItem" ); // for test coverage of list

        tester.clickLink( "tabs:tabs:1:tab-link" );
        tester.assertRenderedPage( ProductPage.class );
        assertEquals( "Pricing", tester.getLastRenderedPage().getPageParameters().getString( "section" ) );

        tester.clickLink( "tabs:tabs:2:tab-link" );
        tester.assertRenderedPage( ProductPage.class );
        assertEquals( "Statistics", tester.getLastRenderedPage().getPageParameters().getString( "section" ) );
        tester.clickLink( "tabs:tabs:0:tab-link" );
        tester.assertRenderedPage( ProductPage.class );
        assertEquals( "Comments", tester.getLastRenderedPage().getPageParameters().getString( "section" ) );
    }

    // TODO fix rename of product and focus

//    @Test
//    @Transactional
//    @Rollback
//    public void renameProduct() {
//        loadPage();
//        assertEquals( 0, getProducts( tester.getLastRenderedPage() ).size() );
//        tester.clickLink( "newItem" );
//        assertEquals( 1, getProducts( tester.getLastRenderedPage() ).size() );
//
//        FormTester form = tester.newFormTester( "list:1:item:form" );
//
//        form.setValue( "name", "newName" );
//        form.submit();
//        assertEquals( "newName", getProducts( tester.getLastRenderedPage() ).get( 0 ).getName() );
//    }

    private List<Product> getProducts( Page page ) {
        ProductPage rp = (ProductPage) page;
        return rp.getProfileModel().getObject().getProducts();
    }
}
