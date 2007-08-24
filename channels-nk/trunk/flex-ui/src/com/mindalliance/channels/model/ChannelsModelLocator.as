
package com.mindalliance.channels.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ScenarioVO;
	
	import mx.resources.ResourceBundle;
	import com.mindalliance.channels.model.flowmap.FlowMapModel;
    /**
     * This singleton class provides references to the models for various components.  In
     * addition, it keeps track of global data elements such as the loaded scenario.
     */
    [Bindable]
	public class ChannelsModelLocator implements IModelLocator
	{
		
 	    [ResourceBundle("services")]
     	private static var serviceResources:ResourceBundle;
		
		// Component Models
		public var projectScenarioBrowserModel : ProjectScenarioBrowserModel = new ProjectScenarioBrowserModel();
		public var permissionModel : PermissionModel = new PermissionModel();
		public var flowMapModel : FlowMapModel = new FlowMapModel();
		
		// Global Properties
		private var _currentProject : ProjectVO;
		private var _currentScenario : ScenarioVO;
		private var _username : String= 'John Doe';
		
 	    private var _urlRoot : String = serviceResources.getString("urlRoot");		
		private var _debug : Boolean = serviceResources.getBoolean("debug");
		// Accessor Functions
		/**
		 * The currently loaded project.
		 */
		public function get currentProject() : ProjectVO {
			return _currentProject;	
		}
		
		/**
		 * @private
		 */
		public function set currentProject(currentProject : ProjectVO) : void {
			this._currentProject = currentProject;	
		}
		/**
		 * The loaded scenario
		 */
		public function get currentScenario() : ScenarioVO {
			return _currentScenario;	
		}
		
		/**
		 * @private
		 */
		public function set currentScenario(currentScenario : ScenarioVO) : void {
			this._currentScenario = currentScenario;	
		}
		
		/**
		 * The username of the logged in user
		 */
		public function get username() : String {
			return _username;	
		}
		/**
		 * @private
		 */		
		public function set username(username : String) : void {
			this._username = username;	
		}
		
		public function get urlRoot() : String {
			return _urlRoot;
		}
		
		public function get debug() : Boolean {
			return _debug;	
		}
		
		public function set debug(debug : Boolean) : void {
			this._debug = debug;	
		}
		
		/**
		 * Singleton instance of ChannelsModelLocator
		 */
		private static var instance:ChannelsModelLocator;

		/**
		 * @private
		 */
		public function ChannelsModelLocator(access:Private)
		{
			if (access != null)
			{
				if (instance == null)
				{
					instance = this;
				}
			}
			else
			{
				throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "ChannelsModelLocator" );
			}
		}
		 
		/**
		 * Returns the Singleton instance of ChannelsModelLocator
		 */
		public static function getInstance() : ChannelsModelLocator
		{
			if (instance == null)
			{
				instance = new ChannelsModelLocator( new Private );
			}
			return instance;
		}
	}
}

/**
 * @private
 * Inner class which restricts contructor access to Private
 */
class Private {}

