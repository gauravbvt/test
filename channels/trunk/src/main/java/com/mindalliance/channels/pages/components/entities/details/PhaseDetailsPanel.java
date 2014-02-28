package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.Set;

/**
 * Phase details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 22, 2009
 * Time: 9:27:31 AM
 */
public class PhaseDetailsPanel extends EntityDetailsPanel implements Guidable {

    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;


    public PhaseDetailsPanel( String id, PropertyModel<ModelEntity> entityModel, Set<Long> expansions ) {
        super( id, entityModel, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-phase";
    }


    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( final WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addTimingChoice();
    }


    private void addTimingChoice() {
        DropDownChoice<Phase.Timing> timingChoices = new DropDownChoice<Phase.Timing>(
                "phaseChoices",
                new PropertyModel<Phase.Timing>( this, "timing" ),
                Arrays.asList( Phase.Timing.values() ),
                new ChoiceRenderer<Phase.Timing>() {
                    public Object getDisplayValue( Phase.Timing timing ) {
                        return timing.getLabel();
                    }
                }
        );
        timingChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPhase() ) );
            }
        } );
        timingChoices.setEnabled( isLockedByUser( getPhase() ) );
        moDetailsDiv.add( timingChoices );
    }

    /**
     * Get phase event timing.
     * @return an event timing
     */
    public Phase.Timing getTiming() {
        return getPhase().getTiming();
    }

    /**
     * Set phase event timing.
     * @param timing an event timing
     */
    public void setTiming( Phase.Timing timing ) {
        doCommand( new UpdateModelObject( getUser().getUsername(), getPhase(), "timing", timing ) );
    }

    private Phase getPhase() {
        return (Phase) getEntity();
    }


}
