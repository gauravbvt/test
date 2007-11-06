// Borrowed and customized from 'Professional Flex 2'

package logging
{

import flash.events.*;
import flash.net.LocalConnection;

import mx.logging.*;
import mx.logging.targets.*;

/**
 *  Provides a logger target that outputs to a <code>LocalConnection</code>,
 *  connected to the logging application.
 */
public class LocalConnectionTarget extends LineFormattedTarget 
{

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
	 *  <p>Constructs an instance of a logger target that will send
	 *  the log data to the Debug application.</p>
	 *
     *  @param defines the Debug application connection string
     *
     *  @param defines what method to call on the Debug application connection.
     */
    public function LocalConnectionTarget() 
    {
		super();
		lc = new LocalConnection();
        lc.addEventListener(StatusEvent.STATUS, statusEventHandler);
        this.connection = "_loggingReceiver";
        this.method = "logMessage";
    }
    
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var lc:LocalConnection;
    
    /**
     *  @private
     *  The name of the method to call on the Debug application connection.
     */
    private var method:String;

    /**
     *  @private
     *  The name of the connection string to the Debug application.
     */
    private var connection:String;    

	//--------------------------------------------------------------------------
	//
	//  EventListener
	//
	//--------------------------------------------------------------------------

	/**
	 * 	Supress common LocalConnection status message from bubbling up
	 * 	as errors
	 */
    private function statusEventHandler(event:StatusEvent):void 
    {
        //trace("statusEventHandler: " + event.code);
    }


	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 * 	Override the Event levels to work with each debug application correctly
	 */	
    override public function logEvent(event:LogEvent):void
    {
		var level:int = event.level;    	
		if (event.level == LogEventLevel.ALL || 
			event.level == LogEventLevel.INFO)
			level = 1;
		else if (event.level == LogEventLevel.DEBUG)
			level = 2;
		else if (event.level == LogEventLevel.WARN)
			level = 4;
		else if (event.level == LogEventLevel.ERROR ||
			event.level == LogEventLevel.FATAL)
			level = 8;

    	var date:String = "";
    	if (includeDate || includeTime)
    	{
    		var d:Date = new Date();
    		if (includeDate)
    		{
    			date = Number(d.getUTCMonth() + 1).toString() + "/" +
					   d.getUTCDate().toString() + "/" + 
					   d.getUTCFullYear();
    		}	
    		if (includeTime)
    		{
    			date = pad(d.getUTCHours()) + ":" +
					   pad(d.getUTCMinutes()) + ":" +
					   pad(d.getUTCSeconds()) + "." +
					   pad(d.getUTCMilliseconds());
    		}
    	}

 		var category:String = includeCategory ?
							  ILogger(event.target).category :
							  "";
// Method sendLog 
// @param1 date
// @param2 category
// @param3 level
// @param4 msg
// @param4 type
    	//internalLog(date + category + event.message);
    	try 
    	{
	    	lc.send(connection, method, date, category, level, event.message);
	    } 
	    catch(error:Error) 
	    {
	    }
    }

    /**
	 *  @private
	 */
	private function pad(num:Number):String
    {
        return num > 9 ? num.toString() : "0" + num.toString();
    }    
    
    public function toString():String
    {
    	return "LocalConnectionTarget[" + this.id + "]";
    }
}
}