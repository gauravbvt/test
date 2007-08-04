
package @namespace@.@model@
{
	import com.adobe.cairngorm.model.ModelLocator;
    
    [Bindable]
	public class @projectname@ModelLocator implements ModelLocator
	{
		/**
		 * Singleton instance of @projectname@ModelLocator
		 */
		private static var instance:@projectname@ModelLocator;

		public function @projectname@ModelLocator(access:Private)
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
				throw new Error( "SINGLETON_EXCEPTION: @projectname@ModelLocator" );
			}
		}
		 
		/**
		 * Returns the Singleton instance of @projectname@ModelLocator
		 */
		public static function getInstance() : @projectname@ModelLocator
		{
			if (instance == null)
			{
				instance = new @projectname@ModelLocator( new Private );
			}
			return instance;
		}
	}
}

/**
 * Inner class which restricts contructor access to Private
 */
class Private {}

