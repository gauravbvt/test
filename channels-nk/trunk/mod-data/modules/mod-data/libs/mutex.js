// Implements http://www.eonclash.com/Tutorials/Multithreading/MartinHarvey1.1/Ch11.html
/*
    * Read operations can execute concurrently.
    * Write operations cannot execute at the same time as read operations.
    * Write operations cannot execute at the same time as write operations.
    * 
    * There is an asymmetry in the synchronization scheme: threads potentially wanting to read 
    * will block before reading if there are any active writers, whilst threads wanting to write 
    * block before writing if there are any reading readers. This gives priority to writing threads; 
    * a sensible approach, given that writes are less frequent than reads.
 */
 
LOG_MUTEX = true;

function issueMutexRequest(command, who) {
	var req = context.createSubRequest("active:MREWSynchronizer");
	req.addArgument("operator", new StringAspect(command));
	if (LOG_MUTEX) log(who + ": Start " + command, "info");
	context.issueSubRequest(req);
	if (LOG_MUTEX) log(who + ": End " + command, "info");
}

// Multiple reads, exclusive write

function beginRead(who) {
	issueMutexRequest("beginRead", who);
}

function endRead(who) {
	issueMutexRequest("endRead", who);
}

function beginWrite(who) {
	issueMutexRequest("beginWrite", who);
}

function endWrite(who) {
	issueMutexRequest("endWrite", who);
}
