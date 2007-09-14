package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class AgentVO extends ElementVO implements IValueObject
	{
		
		public function AgentVO(id : String,
		                          name : String,
		                          description : String,
		                          task : ElementVO,
		                          role : ElementVO) {
		  super(id, name, description);
		  this.task = task;
		  this.role = role;                          	
        }
		private var _task : ElementVO;
		private var _role : ElementVO;
		
		public function get task() : ElementVO {
			return _task;
		}

		public function set task(task : ElementVO) : void {
			_task=task;
		}
		
		public function get role() : ElementVO {
			return _role;
		}

		public function set role(role : ElementVO) : void {
			_role=role;
		}
		
	}
}