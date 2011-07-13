package com.mindalliance.globallibrary;

public class DriverScriptFunctionLibrary {
	public static void callReflexClass() {
		try {
			Object[] testCaseId = new Object[10];
			testCaseId[0] = "MAC0001_UndoAddSegment";
			@SuppressWarnings("rawtypes")
			Class c;
			GenericFunctionLibrary.initializeTestData();
			for (int i = 0;i <= testCaseId.length ; i++) {
				c = Class.forName("com.mindalliance.testscripts." + testCaseId[i]);
				c.newInstance();
			}		           
				 
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
	//public static void main(String args[]) {
		//DriverScriptFunctionLibrary d = new DriverScriptFunctionLibrary();
		//d.callReflexClass();
	//}

	/*public static void main(String[] args) {
	    try {
	      // Creates an object of type Class which contains the information of 
	      // the class String
	      Class cl = Class.forName("java.lang.String");

	      // getDeclaredFields() returns all the constructors of the class.
	      Constructor cnst[] = cl.getConstructors();

	      // getFields() returns all the declared fields of the class.
	      Field fld[] = cl.getDeclaredFields();

	      // getMethods() returns all the declared methods of the class.
	      Method mtd[] = cl.getMethods();
	      System.out.println("Name of the Constructors of the String class");

	      for (int i = 0; i < cnst.length; i++) {
	        System.out.println(cnst[i].getName());
	      }

	      System.out.println("Name of the Declared fields");

	      for (int i = 0; i < fld.length; i++) {
	        System.out.println(fld[i].getName());
	      }

	      System.out.println("Name of the Methods");

	      for (int i = 0; i < mtd.length; i++) {
	        System.out.println(mtd[i].getName());
	      }

	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	}
}
*/