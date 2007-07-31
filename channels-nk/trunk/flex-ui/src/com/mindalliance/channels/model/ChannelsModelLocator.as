
package com.mindalliance.ui.model
{
	import com.adobe.cairngorm.model.IModelLocator;
	import com.adobe.cairngorm.CairngormError;
    import com.adobe.cairngorm.CairngormMessageCodes;
    
    [Bindable]
	public class ChannelsModelLocator implements IModelLocator
	{
		/**
		 * Singleton instance of ChannelsModelLocator
		 */
		private static var instance:ChannelsModelLocator;

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
 * Inner class which restricts contructor access to Private
 */
class Private {}

