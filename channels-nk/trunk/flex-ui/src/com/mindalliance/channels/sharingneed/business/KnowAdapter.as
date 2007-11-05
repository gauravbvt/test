package com.mindalliance.channels.sharingneed.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.sharingneed.events.CreateNeedToKnowSequenceEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.KnowVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.Knowable;
	import com.mindalliance.channels.vo.common.SourceOrSink;

	public class KnowAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function KnowAdapter()
		{
			super("know", KnowVO);
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
		
		override public function create(params:Object):XML
		{
			return <know schema="/channels/schema/know.rng">
                        <who><{params["who"].type + "Id"}>{params["who"].id}</{params["who"].type + "Id"}></who>
                        <about><{params["about"].type + "Id"}>{params["about"].id}</{params["about"].type + "Id"}></about>
                    </know>;
		}
		
        override public function postCreate(element : ElementVO, parameters : Object) : void {
                CairngormHelper.fireEvent(new CreateNeedToKnowSequenceEvent(
                                                parameters["needToKnowWho"], 
                                                parameters["needToKnowAbout"], 
                                                element.id));        
        }
        override public function updateElement(element : ElementVO, values : Object) : void {
            var data : KnowVO = element as KnowVO;
            data.who = values["who"];
            data.about = values["about"];
            data.what = values["what"];
        }    
		
	}
}