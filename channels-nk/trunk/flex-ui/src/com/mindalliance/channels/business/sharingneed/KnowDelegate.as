// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.sharingneed
{
	import com.mindalliance.channels.business.common.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.KnowVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.Knowable;
	import com.mindalliance.channels.vo.common.SourceOrSink;
	
	import mx.rpc.IResponder;
	
	public class KnowDelegate extends BaseDelegate
	{	
		public function KnowDelegate(responder:IResponder)
		{
			super(responder);
			typeName="know";
		}
		/**
         * parses /channels/schema/Know.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			var who : SourceOrSink;
			
			if (xml.who.agentId.length() != 0) {
                who = SourceOrSink.getAgentType(xml.who.agentId);
			} else if (xml.who.repositoryId.length() != 0) {
                who = SourceOrSink.getRepositoryType(xml.who.repositoryId);
			} if (xml.who.roleId.length() != 0) {
                who = SourceOrSink.getRoleType(xml.who.roleId);
			}
			
			var about : Knowable;
			if (xml.about.acquirementId.length() != 0) {
				about = Knowable.getAcquirementType(xml.about.acquirementId);
			} else if (xml.about.artifactId.length() != 0) {
                about = Knowable.getArtifactType(xml.about.artifactId);
			} else if (xml.about.eventId.length() != 0) {
                about = Knowable.getEventType(xml.about.eventId);
				
			} else if (xml.about.taskId.length() != 0) {
                about = Knowable.getTaskType(xml.about.taskId);
				
			}
			var what : InformationVO;
			
			if (xml.what.length() != 0) {
                what = XMLHelper.xmlToInformation(xml.what.information[0]);	
			}
			return new KnowVO(xml.id, who,about,what);
		}
		/**
         * generates /channels/schema/Know.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : KnowVO = (element as KnowVO);
			var xml : XML = <know schema="/channels/schema/know.rng">
						<id>{obj.id}</id>
                        <who><{obj.who.type + "Id"}>{obj.who.id}</{obj.who.type + "Id"}></who>
                        <about><{obj.about.type + "Id"}>{obj.about.id}</{obj.about.type + "Id"}></about>
					</know>;
			if (obj.what != null) {
				var what : XML = <what></what>;
				what.appendChild(XMLHelper.informationToXML(obj.what));
				xml.appendChild(what);
			}
			return xml;
		}
		public function create(who : SourceOrSink, about : Knowable, param : Array = null) : void {
              if (param == null) {
                param  = new Array();
              }
              param["who"] = who;
              param["about"] = about;
              var xml : XML = <know schema="/channels/schema/know.rng">
                        <who><{who.type + "Id"}>{who.id}</{who.type + "Id"}></who>
                        <about><{about.type + "Id"}>{about.id}</{about.type + "Id"}></about>
                    </know>;
              createElement(xml,param);
            
        }
		
	}
}