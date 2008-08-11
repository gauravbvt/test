package com.mindalliance.channels.playbook.pages.forms.tabs.networking;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.graph.support.Networking;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and Confidential. User: jf Date: Jul 29,
 * 2008 Time: 10:58:30 AM
 */
public class NetworkingBasicTab extends AbstractFormTab {

    private Networking networking;
    private RefreshingView<Ref> agreementsView;
    private RefreshingView<Ref> relationshipsView;
    private RefreshingView<Ref> flowsView;
    private static final long serialVersionUID = -861232619548475750L;

    public NetworkingBasicTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        networking = (Networking) getElement().deref();
        AjaxLink<?> fromResourceLink = new AjaxLink( "fromResourceLink" ) {
            private static final long serialVersionUID = 4946836796769745510L;

            @Override
            public void onClick( AjaxRequestTarget target ) {
                edit( networking.getFromResource(), target );
            }
        };
        fromResourceLink.add( new Label( "fromResource", new Model<String>(
                (String) RefUtils.get( networking.getFromResource(), "name" ) ) ) );
        add( fromResourceLink );

        AjaxLink<?> toResourceLink = new AjaxLink( "toResourceLink" ) {
            private static final long serialVersionUID = 1194233347779221982L;

            @Override
            public void onClick( AjaxRequestTarget target ) {
                edit( networking.getToResource(), target );
            }
        };
        toResourceLink.add( new Label( "toResource", new Model<String>(
                (String) RefUtils.get( networking.getToResource(), "name" ) ) ) );
        add( toResourceLink );

        WebMarkupContainer accessAndJobDiv = new WebMarkupContainer( "accessAndJobDiv" );
        setVisibility( accessAndJobDiv, networking.hasAccess() || networking.hasJobWith() );
        add( accessAndJobDiv );

        WebMarkupContainer hasJobDiv = new WebMarkupContainer( "hasJobDiv" );
        setVisibility( hasJobDiv, networking.hasJobWith() );
        hasJobDiv.add( new Label( "fromResourceJob", new Model<String>(
                (String) RefUtils.get( networking.getFromResource(), "name" ) ) ) );
        hasJobDiv.add( new Label( "toResourceJob",
                                  new Model<String>( (String) RefUtils.get( networking.getToResource(), "name" ) ) ) );
        accessAndJobDiv.add( hasJobDiv );

        WebMarkupContainer hasAccessDiv = new WebMarkupContainer( "hasAccessDiv" );
        setVisibility( hasAccessDiv, networking.hasAccess() );
        hasAccessDiv.add( new Label( "fromResourceAccess", new Model<String>(
                (String) RefUtils.get( networking.getFromResource(), "name" ) ) ) );
        hasAccessDiv.add( new Label( "toResourceAccess", new Model<String>(
                (String) RefUtils.get( networking.getToResource(), "name" ) ) ) );
        accessAndJobDiv.add( hasAccessDiv );

        WebMarkupContainer agreementsDiv = new WebMarkupContainer( "agreementsDiv" );
        setVisibility( agreementsDiv, !networking.getAgreements().isEmpty() );
        add( agreementsDiv );

        agreementsView = new RefreshingView<Ref>( "agreements", new Model<Serializable>(
                (Serializable) networking.getAgreements() ) ) {
            private static final long serialVersionUID = 4118515098233355340L;

            @Override
            protected Iterator<IModel<Ref>> getItemModels() {
                List<Ref> agreements = (List<Ref>) agreementsView.getDefaultModelObject();
                return new ModelIteratorAdapter<Ref>( agreements.iterator() ) {
                    @Override
                    protected IModel<Ref> model( Ref object ) {
                        return new RefModel( object );
                    }
                };
            }

            @Override
            protected void populateItem( Item<Ref> item ) {
                final Ref agreement = item.getModelObject();
                AjaxLink<?> agreementLink = new AjaxLink( "agreementLink" ) {
                    // NON-NLS
                    private static final long serialVersionUID = 3399671396919220120L;

                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        edit( agreement, target );
                    }
                };
                Label agreementString = new Label( "agreement", agreement.deref().about() );
                agreementLink.add( agreementString );
                item.add( agreementLink );
            }
        };
        agreementsDiv.add( agreementsView );

        WebMarkupContainer relationshipsDiv = new WebMarkupContainer( "relationshipsDiv" );
        setVisibility( relationshipsDiv, !networking.getRelationships().isEmpty() );
        add( relationshipsDiv );
        relationshipsView = new RefreshingView<Ref>( "relationships", new Model<Serializable>(
                (Serializable) networking.getRelationships() ) ) {
            private static final long serialVersionUID = -5797876356831379791L;

            @Override
            protected Iterator<IModel<Ref>> getItemModels() {
                List<Ref> relationships = (List<Ref>) relationshipsView.getDefaultModelObject();
                return new ModelIteratorAdapter<Ref>( relationships.iterator() ) {
                    @Override
                    protected IModel<Ref> model( Ref object ) {
                        return new RefModel( object );
                    }
                };
            }

            @Override
            protected void populateItem( Item<Ref> item ) {
                final Ref relationship = item.getModelObject();
                AjaxLink<?> relationshipLink = new AjaxLink( "relationshipLink" ) {
                    private static final long serialVersionUID = 4628314579650647543L;

                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        edit( relationship, target );
                    }
                };
                Label relationshipString = new Label( "relationship", relationship.deref().about() );
                relationshipLink.add( relationshipString );
                item.add( relationshipLink );
            }
        };
        relationshipsDiv.add( relationshipsView );

        WebMarkupContainer flowsDiv = new WebMarkupContainer( "flowsDiv" );
        setVisibility( flowsDiv, !networking.getFlowActs().isEmpty() );
        add( flowsDiv );
        flowsView = new RefreshingView<Ref>( "flowActs",
                                             new Model<Serializable>( (Serializable) networking.getFlowActs() ) ) {
            private static final long serialVersionUID = 5647693305839268716L;

            @Override
            protected Iterator<IModel<Ref>> getItemModels() {
                List<Ref> flowActs = (List<Ref>) flowsView.getDefaultModelObject();
                return new ModelIteratorAdapter<Ref>( flowActs.iterator() ) {
                    @Override
                    protected IModel<Ref> model( Ref object ) {
                        return new RefModel( object );
                    }
                };
            }

            @Override
            protected void populateItem( Item<Ref> item ) {
                final Ref flowAct = item.getModelObject();
                AjaxLink<?> flowActLink = new AjaxLink( "flowActLink" ) {
                    private static final long serialVersionUID = -6988662846808852426L;

                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        edit( flowAct, target );
                    }
                };
                Label flowActString = new Label( "flowAct", flowAct.deref().about() );
                flowActLink.add( flowActString );
                item.add( flowActLink );
            }
        };
        flowsDiv.add( flowsView );
    }
}
