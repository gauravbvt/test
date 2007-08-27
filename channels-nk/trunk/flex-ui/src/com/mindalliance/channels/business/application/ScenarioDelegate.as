// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.application
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.ScenarioVO;
	import com.mindalliance.channels.vo.ElementVO;
	
	public class ScenarioDelegate extends BaseDelegate
	{ 
		public function ScenarioDelegate(responder:IResponder)
		{
			super(responder);
			typeName="scenario";
		}
		
		public function getScenarioList(projectId : String) : void {
			
			var request:Array = new Array();
			request["projectId"] = projectId;

			performQuery("allScenariosInProject", request);
		}
		public function createScenario(name:String, projectId:String) : void {
			var scenario : XML = <scenario></scenario>;
			scenario.appendChild(<name>{name}</name>);
			scenario.appendChild(<projectId>{projectId}</projectId>);

			createElement(scenario);	
		}
		
		/**
		 * Produces XML of the form:
		 * 
		 * <scenario>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </scenario>
		 */
		override public function toXML(element : ElementVO) : XML {
			var obj : ScenarioVO = (element as ScenarioVO)
			return <scenario schema="/channels/schema/scenario.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
						<projectId>{obj.project.id}</projectId>
					</scenario>;
		
		}
		/**
		 * Expects XML of the form:
		 * <scenario>
		 *   <id>{id}</id>
		 *   <name>{name}</id>
		 *   <description>{description}</description>
		 *   <projectId>{projectId}</projectId>
		 * </scenario>
		 */
		override public function fromXML( obj : Object ) : ElementVO {
				return new ScenarioVO(obj.id, obj.name, obj.description, new ElementVO(obj.projectId, null));
		}
		
	}
}