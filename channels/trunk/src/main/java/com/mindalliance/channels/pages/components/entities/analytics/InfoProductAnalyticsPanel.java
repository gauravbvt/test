package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Guidable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Info product analytics panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 1:43 PM
 */
public class InfoProductAnalyticsPanel extends AbstractUpdatablePanel implements Guidable {


    public InfoProductAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getSectionId() {
        return "analyzing";
    }

    @Override
    public String getTopicId() {
        return "analyzing-entities";
    }

    private void init() {
        addTitle();
        addValueAnalysis();
    }

    private void addTitle() {
        add( new Label( "title", "Value of info product \"" + getInfoProduct().getName() + "\"" ) );
    }

    private void addValueAnalysis() {
        EntityValueAnalysisPanel valuePanel = new EntityValueAnalysisPanel<InfoProduct>(
                "valueAnalysis",
                new PropertyModel<InfoProduct>( this, "infoProduct" ),
                getExpansions()
        );
        add( valuePanel );
    }

    public InfoProduct getInfoProduct() {
        return (InfoProduct) getModel().getObject();
    }

}
