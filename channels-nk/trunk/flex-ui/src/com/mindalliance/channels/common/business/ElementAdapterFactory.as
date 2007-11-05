package com.mindalliance.channels.common.business
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.mindalliance.channels.application.business.ProjectAdapter;
	import com.mindalliance.channels.application.business.ScenarioAdapter;
	import com.mindalliance.channels.categories.business.CategoryAdapter;
	import com.mindalliance.channels.categories.business.DisciplineAdapter;
	import com.mindalliance.channels.people.business.OrganizationAdapter;
	import com.mindalliance.channels.people.business.PersonAdapter;
	import com.mindalliance.channels.people.business.RoleAdapter;
	import com.mindalliance.channels.people.business.UserAdapter;
	import com.mindalliance.channels.resources.business.RepositoryAdapter;
	import com.mindalliance.channels.scenario.business.*;
	import com.mindalliance.channels.sharingneed.business.KnowAdapter;
	import com.mindalliance.channels.sharingneed.business.NeedToKnowAdapter;
	import com.mindalliance.channels.sharingneed.business.SharingNeedAdapter;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flash.utils.getDefinitionByName;
	import flash.utils.getQualifiedClassName;
	public class ElementAdapterFactory
	{
		private static const ADAPTERS : Array = [
		  ProjectAdapter,
		  ScenarioAdapter,
		  CategoryAdapter,
		  DisciplineAdapter,
		  OrganizationAdapter,
		  PersonAdapter,
		  RoleAdapter,
		  UserAdapter,
		  RepositoryAdapter,
		  AcquirementAdapter,
		  AgentAdapter,
		  ArtifactAdapter,
		  EventAdapter,
		  TaskAdapter,
		  KnowAdapter,
		  NeedToKnowAdapter,
		  SharingNeedAdapter
		];
		
		private var keyMap : Array;
		private var typeMap : Array;
		
		private function init() : void {
			keyMap = new Array();
			typeMap = new Array();
			for each (var obj : Class in ADAPTERS) {
				var adapter : IElementAdapter = new obj() as IElementAdapter;
                keyMap[adapter.key] = adapter;	
                typeMap[adapter.type] = adapter;
			}
		}
		
		public function fromKey (key : String) : IElementAdapter {
			if (keyMap == null) init();
			return keyMap[key];
		}
		
		public function fromType(type : ElementVO) : IElementAdapter {
			if (typeMap == null) init();
			return typeMap[flash.utils.getDefinitionByName(flash.utils.getQualifiedClassName(type))];
		}
		
		
		/**
         * Singleton instance of ElementAdapterFactory
         */
        private static var instance:ElementAdapterFactory;

        /**
         * @private
         */
        public function ElementAdapterFactory(access:Private)
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
                throw new CairngormError( CairngormMessageCodes.SINGLETON_EXCEPTION, "ElementAdapterFactory" );
            }
        }
         
        /**
         * Returns the Singleton instance of ChannelsModelLocator
         */
        public static function getInstance() : ElementAdapterFactory
        {
            if (instance == null)
            {
                instance = new ElementAdapterFactory( new Private );
            }
            return instance;
        }
    }
}

class Private {}