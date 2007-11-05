package com.mindalliance.channels.sharingneed.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.sharingneed.events.CreateSharingNeedSequenceEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.NeedToKnowVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.Knowable;
	import com.mindalliance.channels.vo.common.SourceOrSink;

	public class NeedToKnowAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function NeedToKnowAdapter()
		{
			super("needToKnow", NeedToKnowVO);
		}
		
		/**
         * parses /channels/schema/NeedToKnow.rng
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
            
            var updateOnChange : Boolean = false;
            var updateEvery : DurationVO;
            if (xml.delivery.updating.length() != 0) {
                if (xml.delivery.updating.onChange.length() != 0) {
                    updateOnChange = true;
                } else if (xml.delivery.updating.every.length() != 0) {
                    updateEvery = XMLHelper.xmlToDuration(xml.delivery.updating.every.duration);
                    
                }
            }
            
            return new NeedToKnowVO(xml.id, who,about,what,
                                    xml.criticality.level,
                                    XMLHelper.xmlToDuration(xml.urgency.duration),
                                    xml.delivery.@mode,
                                    updateOnChange,
                                    updateEvery,
                                    XMLHelper.xmlToCategorySet(xml.delivery.format[0]));
        }
        /**
         * generates /channels/schema/NeedToKnow.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : NeedToKnowVO = (element as NeedToKnowVO);
            var xml : XML = <needToKnow schema="/channels/schema/needToKnow.rng">
                        <id>{obj.id}</id>
                        <who><{obj.who.type + "Id"}>{obj.who.id}</{obj.who.type + "Id"}></who>
                        <about><{obj.about.type + "Id"}>{obj.about.id}</{obj.about.type + "Id"}></about>
                    </needToKnow>;
                    
                    
            if (obj.what != null) {
                var what : XML = <what></what>;
                what.appendChild(XMLHelper.informationToXML(obj.what));
                xml.appendChild(what);
            }
            xml.appendChild(<criticality><level>{obj.criticality}</level></criticality>);
            var urgency : XML =<urgency></urgency>
            urgency.appendChild(XMLHelper.durationToXML(obj.urgency));
            xml.appendChild(urgency);
            var delivery : XML = <delivery mode={obj.deliveryMode}></delivery>;
            if (obj.updateOnChange || obj.updateEvery != null) {
                var updating : XML = <updating></updating>;
                if (obj.updateOnChange)
                    updating.appendChild(<onChange/>);
                else {
                    var every : XML = <every></every>
                    every.appendChild(XMLHelper.durationToXML(obj.updateEvery));
                    updating.appendChild(every);
                }
                delivery.appendChild(updating);

            }
            var format : XML = <format></format>
            format.appendChild(XMLHelper.categorySetToXML(obj.format));
            delivery.appendChild(format);
            xml.appendChild(delivery);
            return xml;
        }
		
		override public function create(params:Object):XML
		{
			var who : SourceOrSink = params["who"] as SourceOrSink;
			var about : Knowable = params["about"] as Knowable;
			return <needToKnow schema="/channels/schema/needToKnow.rng">
                        <who><{params["who"].type + "Id"}>{params["who"].id}</{params["who"].type + "Id"}></who>
                        <about><{about.type + "Id"}>{about.id}</{about.type + "Id"}></about>
                        <criticality><level>low</level></criticality>
                        <urgency><duration><value>0</value><unit>minute</unit></duration></urgency>
                        <delivery mode="notify">
                          <format>
                            <categories atMostOne="false" taxonomy="format"></categories>
                          </format>
                        </delivery>
                    </needToKnow>;
		}
		
		override public function postCreate(element : ElementVO, parameters : Object) : void {
             CairngormHelper.fireEvent(new CreateSharingNeedSequenceEvent(
                                                parameters["knowId"],
                                                element.id));
        } 
        override public function updateElement(element : ElementVO, values : Object) : void {
            var data : NeedToKnowVO = element as NeedToKnowVO;
            data.who = values["who"];
            data.about = values["about"];
            data.what = values["what"];
            data.criticality = values["criticality"];
            data.urgency = values["urgency"];
            data.deliveryMode = values["deliveryMode"];
            data.updateOnChange = values["updateOnChange"];
            data.updateEvery = values["updateEvery"];
            data.format = values["format"];            
        }   		
	}
}