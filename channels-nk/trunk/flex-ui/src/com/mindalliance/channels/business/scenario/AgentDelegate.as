package com.mindalliance.channels.business.scenario
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.vo.AgentVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;

	public class AgentDelegate extends BaseDelegate
	{
		public function AgentDelegate(responder : IResponder)
		{
		    super(responder);
		    typeName = "agent";
		}
		
		public function getAgentList(taskId : String) : void {
		  	var request:Array = new Array();
            request["taskId"] = taskId;
            performQuery("taskAgents", request);
            
		}
		
		public function getAgentListByScenarioId(scenarioId : String) : void {
            var request:Array = new Array();
            request["scenarioId"] = scenarioId;
            performQuery("agentsInScenario", request);
		}
		
		override public function fromXML(obj : XML) : ElementVO {
			return new AgentVO(obj.id,obj.name,obj.description,new ElementVO(obj.taskId, null), new ElementVO(obj.roleId, null));
		}
		
		override public function toXML(element : ElementVO) : XML {
		  var obj : AgentVO = (element as AgentVO);
		  return <agent schema="/channels/schema/agent.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                        <taskId>{obj.task.id}</taskId>
                        <roleId>{obj.role.id}</roleId>
		                  </agent>;	
		}
		
		override public function fromXMLElementList(list:XML):ArrayCollection {
		    var result : ArrayCollection = new ArrayCollection();
		    for each (var el : XML in list.agent) {	
                result.addItem(fromXML(el));
		    }
		    return result;
		}
		
		public function create(name : String, taskId : String, roleId : String) : void {
            var param : Array=new Array();
            param["name"] = name;
            param["taskId"] = taskId;
            param["roleId"] = roleId;
            var xml : XML = <agent>
                        <name>{name}</name>
                        <taskId>{taskId}</taskId>
                        <roleId>{roleId}</roleId>
                          </agent>;
            createElement( xml, param);
        }
	}
}