package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Info required panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/3/11
 * Time: 3:19 PM
 */
public class InfoRequiredPanel extends AbstractCommandablePanel {
    private TextField<String> infoField;

    public InfoRequiredPanel( String id, IModel<Requirement> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        addInfo();
        addInfoTags();
        adjustFields();
    }

    private void adjustFields() {
        Requirement requirement = getRequirement();
        infoField.setEnabled( isLockedByUser( requirement ) );
    }

    private void addInfo() {
        infoField = new TextField<String>(
                "information",
                new PropertyModel<String>( this, "information" ) );
        infoField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getRequirement(), "info" ) );
            }
        } );
        add( infoField );
    }

    private void addInfoTags() {
        AjaxFallbackLink tagsLink = new AjaxFallbackLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPlan(), PlanEditPanel.TAGS ) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", true, new Model<String>( "model-object-link" ) ) );
        add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "infoTags", new Model<Taggable>( getRequirement() ), "infoTags" );
        add( tagsPanel );
    }



    public String getInformation() {
        return getRequirement().getInformation();
    }

    public void setInformation( String val ) {
        if ( val != null && !val.trim().isEmpty() ) {
            String info = val.trim();
            doCommand( new UpdatePlanObject( getUsername(), getRequirement(), "information", info ) );
        }
    }
    private Requirement getRequirement() {
         return (Requirement) getModel().getObject();
     }

}
