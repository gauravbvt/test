package com.mindalliance.channels.pages.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Modalable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.rfi.RFI;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * My rfis page.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/12
 * Time: 1:31 PM
 */
public class RFIsPage extends AbstractChannelsWebPage implements Modalable {

    private RFI selectedRFI;
    private Component rfiPanel;
    /**
     * Modal dialog window.
     */
    private ModalWindow dialogWindow;
    private UserRFIsPanel userRFIsPanel;

    public RFIsPage() {
        this( new PageParameters() );
    }

    public RFIsPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        addHeading();
        addModalDialog( "dialog", null, this );
        addUserRFIsPanel();
        addRFIPanel();
    }

    private void addHeading() {
        add( new Label( "planName", getPlan().getName() ) );
        add( new Label( "planVersion", "v" + getPlan().getVersion() ) );
        add( new UserFeedbackPanel(
                "feedback",
                getPlan(),
                "Send feedback",
                Feedback.ISSUES ) );
        add( new Label( "planDescription", getPlan().getName() ) );
    }

    private void addUserRFIsPanel() {
        userRFIsPanel = new UserRFIsPanel( "rfis", new PropertyModel<RFI>( this, "selectedRFI"  ) );
        addOrReplace( userRFIsPanel );
    }

    private void addRFIPanel() {
        if ( selectedRFI == null ) {
            rfiPanel = new Label( "rfi", "" );
        } else {
            rfiPanel = new RFIPanel( "rfi", new Model<RFI>( selectedRFI ) );
        }
        rfiPanel.setOutputMarkupId( true );
        makeVisible( rfiPanel, selectedRFI != null );
        addOrReplace( rfiPanel );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( RFI.class ) ) {
            if ( change.isExpanded() ) {
                RFI rfi = (RFI) change.getSubject( getQueryService() );
                if ( selectedRFI != null && rfi.equals(  selectedRFI ) ) {
                    selectedRFI = null;
                } else {
                    selectedRFI = rfi;
                }
            } else if ( change.isCollapsed() ) {
                selectedRFI = null;
            }
        } else if ( change.isRefreshNeeded() ) {
            selectedRFI = null;
        } else {
            super.changed( change );
        }
    }


    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        if ( change.isRefreshNeeded() ) {
            addRFIPanel();
            target.add( rfiPanel );
        } else if ( change.isForInstanceOf( RFI.class ) ) {
            if ( change.isExpanded() || change.isCollapsed() ) {
                addRFIPanel();
                target.add( rfiPanel );
                if ( change.isCollapsed() ) {
                    userRFIsPanel.refresh( target, change );
                }
            } else if ( change.isUpdated() ) {
                userRFIsPanel.refresh( target, change );
                if ( change.isForProperty( "declined" )
                        || change.isForProperty( "accepted" )
                        || change.isForProperty( "submitted" ) ) {
                    selectedRFI = null;
                    addRFIPanel();
                    target.add( rfiPanel );
                }
            }
        } else {
            super.updateWith( target, change, updatables );
        }
    }

    public RFI getSelectedRFI() {
        return selectedRFI;
    }

}
