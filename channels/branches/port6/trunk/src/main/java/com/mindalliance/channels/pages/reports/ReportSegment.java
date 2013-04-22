package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.model.Segment;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/28/11
 * Time: 5:14 PM
 */
public class ReportSegment implements Serializable {

     private final int seq;
     private final Segment segment;

     public ReportSegment(
         int seq,
         Segment segment ) {

         this.seq = seq;
         this.segment = segment;

     }


     public int getSeq() {
         return seq;
     }


     public String getName() {
         return segment.getName();
     }

     public String getTitle() {
         return "Situation " + seq;
     }

     public IModel<String> getLink() {
         return new Model<String>( "#ep_" + seq );
     }

     public IModel<String> getAnchor() {
         return new Model<String>( "ep_" + seq );
     }

     public String getDescription() {
         return segment.getDescription();
     }


     public String getContext() {
         return segment.getPhaseEventTitle();
     }

     public void addLinkTo( WebMarkupContainer item ) {

         item.add(
                 new Label( "segmentName", getName() ),
                 new WebMarkupContainer( "segmentLink" )
                         .add( new Label( "segmentLinkText",
                                 getTitle() ).setRenderBodyOnly( true ) )
                         .add( new AttributeModifier( "href", getLink() ) ) );
     }

     public Segment getSegment() {
         return segment;
     }
 }

