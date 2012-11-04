package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.model.EOIsHolder;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.components.EOIsEditPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Info product details panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/1/12
 * Time: 1:23 PM
 */
public class InfoProductDetailsPanel extends EntityDetailsPanel {

    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;

    public InfoProductDetailsPanel( String id, PropertyModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addEOIsEditPanel();
    }

    private void addEOIsEditPanel() {
        EOIsEditPanel eoisEditPanel = new EOIsEditPanel(
                "eois",
                new Model<EOIsHolder>( getInfoProduct() ),
                false,
                getExpansions() );
        moDetailsDiv.add( eoisEditPanel );
    }


    private InfoProduct getInfoProduct() {
        return (InfoProduct) getEntity();
    }

}
