
package com.mindalliance.channels.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import com.mindalliance.channels.model.categories.*;
	import com.mindalliance.channels.model.flowmap.FlowMapModel;
	import com.mindalliance.channels.model.people.*;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ScenarioVO;
	import com.mindalliance.channels.vo.UserVO;
	
	import mx.resources.ResourceBundle;
	
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
		public var projectScenarioBrowserModel : ProjectScenarioBrowserModel = new ProjectScenarioBrowserModel(elements,elementLists);
		public var permissionModel : PermissionModel = new PermissionModel();
		public var flowMapModel : FlowMapModel = new FlowMapModel();

		
		// The element hash
        private var elements : Object = new Object();
		private var elementLists : Object = new Object();
		
		// Global Properties
		public var currentProject : ProjectVO;
		public var currentScenario : ScenarioVO;
		public var user : UserVO = new UserVO("0",'John Doe',null,true);
		
 	    public var urlRoot : String = serviceResources.getString("urlRoot");		
		public var debug : Boolean = serviceResources.getBoolean("debug");
		
		
		public function getEditorModel(type : Class) : IChannelsModel {
            return new type(elements, elementLists);
		}
		
		public function getElementModel(id : String) : ElementModel {
			if (elements[id] == null) {
                elements[id] = new ElementModel();
			}
			return elements[id];
		}
	    public function getElementListModel(key : String) : ElementListModel {
            if (elementLists[key] == null) {
                elementLists[key] = new ElementModel();
            }
            return elementLists[key];
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

