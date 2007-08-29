utilsURI = "ffcpl:/test/utils.js";
context.importLibrary(utilsURI);

importPackage(Packages.org.ten60.netkernel.xml.representation);
importPackage(Packages.com.ten60.netkernel.urii.aspect);
importPackage(Packages.org.apache.xmlbeans);
importPackage(Packages.java.lang);

// Create the categories

var cat_event = <category>
									<name>Event</name>
									<description>The root of the event taxonomy</description>
									<disciplines/>
									<implies/>
									<information>
										<topic>
											<name></name>
											<description></description>
											<eoi>
												<name></name>
												<description></description>
											</eoi>
										</topic>
									</information>
								</category>;
cat_event = createElement(cat_event, "category");

// Create a categorized element

// Get the element's information template