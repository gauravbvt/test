package com.mindalliance.channels.playbook.pages.forms.tabs.organization;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.Component;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 12:45:52 PM
 */
public class OrganizationAgreementsTab extends AbstractFormTab {

    private Component whereSourceView;
    private Component whereRecipientView;
    private static final long serialVersionUID = -6527452369969183810L;

    public OrganizationAgreementsTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        whereSourceView = new RefreshingView<Ref>(
                "agreementsWhereSource", new RefQueryModel(
                getScope(),
                new Query( "findAgreementsWhereSource", getElement() ) ) ) {
            private static final long serialVersionUID = 6065498237705151943L;

            @Override
            @SuppressWarnings( { "unchecked" } )
            protected Iterator<IModel<Ref>> getItemModels() {
                List<Ref> agreements =
                        (List<Ref>) whereSourceView.getDefaultModelObject();
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
                AjaxLink<?> whereSourceLink =
                        new AjaxLink( "whereSourceLink" ) {
                            private static final long serialVersionUID =
                                    -6704558538100335056L;

                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                edit( agreement, target );
                            }
                        };
                Label whereSourceString = new Label(
                        "whereSourceString",
                        (String) RefUtils.get( agreement, "name" ) );
                whereSourceLink.add( whereSourceString );
                item.add( whereSourceLink );
            }
        };
        addReplaceable( whereSourceView );

        whereRecipientView = new RefreshingView<Ref>(
                "agreementsWhereRecipient",
                new RefQueryModel(
                    getScope(),
                    new Query( "findAgreementsWhereRecipient", getElement() ) ) ) {
            private static final long serialVersionUID = 3219878969996778194L;

            @Override
            @SuppressWarnings( { "unchecked" } )
            protected Iterator<IModel<Ref>> getItemModels() {
                List<Ref> agreements =
                        (List<Ref>) whereRecipientView.getDefaultModelObject();
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
                AjaxLink<?> whereRecipientLink =
                        new AjaxLink( "whereRecipientLink" ) {
                            private static final long serialVersionUID =
                                    3884368691634698424L;

                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                edit( agreement, target );
                            }
                        };
                Label whereRecipientString = new Label(
                        "whereRecipientString",
                        (String) RefUtils.get( agreement, "name" ) );
                whereRecipientLink.add( whereRecipientString );
                item.add( whereRecipientLink );
            }
        };
        addReplaceable( whereRecipientView );
    }
}
