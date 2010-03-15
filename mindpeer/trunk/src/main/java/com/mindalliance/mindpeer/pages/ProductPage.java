// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.model.CommentFeed;
import com.mindalliance.mindpeer.model.Product;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.pages.components.TabPanel;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The seller's products page.
 */
public class ProductPage extends ItemPage<Product> {

    private static final long serialVersionUID = -3928083273321770553L;

   /**
     * Create a new MindPeerPage instance.
     *
     * @param parameters the given parameters
     */
    public ProductPage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * Get the title of the right-hand list.
     * @return the list title
     */
    @Override
    public String getListTitle() {
        return "Product List";
    }

    @Override
    public Product getEmptyItem() {
        return new Product( "Products"){};
    }

    @Override
    public void renameAndSave( String oldName, Product object ) {
        saveUser( getUser() );
    }

    /**
     * Iterate on the list of products.
     *
     * @param profileModel the given profile
     * @return Iterator<Product>
     */
    @Override
    public Iterator<IModel<Product>> getIterator( IModel<Profile> profileModel ) {
        Profile profile = profileModel.getObject();

        List<IModel<Product>> result = new ArrayList<IModel<Product>>();
        Iterator<Product> products = profile.products();
        while ( products.hasNext() )
            result.add( new Model<Product>( products.next() ) );

        return result.iterator();
    }

    @Override
    public String getDefaultSection() {
        return "Comments";
    }

    @Override
    public Product load( Profile profile, String name ) {
        return profile.getProduct( name );
    }

    @Override
    public void createItem( Profile p, String name ) {
        p.addProduct( new CommentFeed( name ) );
        saveUser( p.getUser() );
    }

    @Override
    protected String newItemName() {
        return "feed";
    }

    @Override
    public TabPanel.Tab[] getTabs() {
        return new TabPanel.Tab[] {
                new TabPanel.Tab( "Comments" ){
                    private static final long serialVersionUID = 525788991253922974L;

                    @Override
                    public Component getComponent( String id ) {
                        return new Label( id, "(List of comments made by this user + entry form)");
                    }
                },
                new TabPanel.Tab( "Pricing" ){
                    private static final long serialVersionUID = -8854132875866270214L;

                    @Override
                    public Component getComponent( String id ) {
                        return new Label( id, "(Free for now)");
                    }
                },
                new TabPanel.Tab( "Statistics" ){
                    private static final long serialVersionUID = 9146261456822318975L;

                    @Override
                    public Component getComponent( String id ) {
                        return new Label( id, "(Imagine some cutesy graphs...)");
                    }
                }
        };
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return "Products";
    }

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return 1;
    }
}
