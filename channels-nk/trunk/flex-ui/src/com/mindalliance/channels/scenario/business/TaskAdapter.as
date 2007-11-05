package com.mindalliance.channels.scenario.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.TaskVO;
	import com.mindalliance.channels.vo.common.CauseVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class TaskAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function TaskAdapter()
		{
			super("task", TaskVO);
		}
		
		/**
         * parses /channels/schema/task.rng
         */
        override public function fromXML(xml : XML):ElementVO {
            var cause : CauseVO;
            if (xml.cause.length() > 0) {
                cause = XMLHelper.xmlToCause(xml);  
            }
            return new TaskVO(xml.id, 
                                 xml.name, 
                                 xml.description,
                                 XMLHelper.xmlToCategorySet(xml),
                                 XMLHelper.xmlToOccurenceWhere(xml),
                                 cause,
                                 XMLHelper.xmlToDuration(xml.duration),
                                 new ElementVO(xml.scenarioId, null));
        }
        /**
         * generates /channels/schema/task.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : TaskVO = (element as TaskVO);
            var xml : XML = <task schema="/channels/schema/task.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                    </task>;
            
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            if (obj.where != null) {
                 xml.appendChild(XMLHelper.occurrenceWhereToXML(obj.where)); 
            }
            xml.appendChild(<scenarioId>{obj.scenario.id}</scenarioId>);
            if (obj.cause != null) {
                xml.appendChild(XMLHelper.causeToXML(obj.cause));    
            }
            if (obj.duration != null) {
                xml.appendChild(XMLHelper.durationToXML(obj.duration));
            }
            return xml;
        }
		
		override public function create(params:Object):XML
		{
			return <task schema="/channels/schema/task.rng">
             <name>{params["name"]}</name>
             <categories atMostOne="false" taxonomy="task"/>
             <scenarioId>{params["scenarioId"]}</scenarioId>
             <duration>
               <value>0</value>
               <unit>second</unit>
             </duration>
           </task>;
		}
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('tasks').data.addItem(element);
        } 
        
        override public function updateElement(element : ElementVO, values : Object) : void  {
            
            var data : TaskVO = element as TaskVO;
            data.name=values["name"];
            data.description = values["description"];
            data.categories = values["categories"]; 
            data.where = values["where"];
            data.cause = values["cause"];
            data.scenario = values["scenario"];
            data.duration = values["duration"];            
        }   				
	}
}