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

function grabLock(lock, who) {
	if (LOG_MUTEX)log(who + ": Grab " + lock, "info");
	var req=context.createSubRequest("active:lock");
	req.addArgument("operand","lock:" + lock);
	context.issueSubRequest(req);	
	if (LOG_MUTEX) log(who + ": Grabbed " + lock, "info");
}

function releaseLock(lock, who) {
	if (LOG_MUTEX) log(who + ": Release " + lock, "info");
	var req=context.createSubRequest("active:unlock");
	req.addArgument("operand","lock:" + lock);
	context.issueSubRequest(req);
	if (LOG_MUTEX) log(who + ": Released " + lock, "info");
}

function counting(counter, op, who) {
	if (LOG_MUTEX) log(who + ": " + op + " " + counter, "info");
	var req = context.createSubRequest("counter:" + counter);
	req.addArgument("operand", new StringAspect(op));
	var res = context.issueSubRequest(req);
	var val = parseInt(context.transrept(res, IAspectString).getString());
	if (LOG_MUTEX) log(who + ": " + op + " " + counter + " => " + val, "info");	
	return val;
}

function incrementCounter(counter, who) {
	return counting(counter, "increment", who);
}

function decrementCounter(counter, who) {
	return counting(counter, "decrement", who);
}

function resetCounter(counter, who) {
	return counting(counter,"reset", who);
}

function getCounter(counter, who) {
	return counting(counter, "get", who);
}

function resetAllCounters() {
	if (LOG_MUTEX) log("Resetting all counters", "info");
	var req = context.createSubRequest("counter:" + counter);
	req.addArgument("operand", new StringAspect("resetAll"));
	context.issueSubRequest(req);
	if (LOG_MUTEX) log("All counters reset", "info");
}

function signal(sem, who) {
	if (LOG_MUTEX) log(who + ": signaling " + sem, "info");
	var req = context.createSubRequest("sem:" + sem);
	req.addArgument("operand", new StringAspect("signal"));
	context.issueSubRequest(req);
	if (LOG_MUTEX) log(who + ": signaled " + sem, "info");
}

function waitOn(sem, who) {
	if (LOG_MUTEX) log(who + ": waiting on " + sem, "info");
	var req = context.createSubRequest("sem:" + sem);
	req.addArgument("operand", new StringAspect("wait"));
	context.issueSubRequest(req);
	if (LOG_MUTEX) log(who + ": done waiting on " + sem, "info");
}


// Multiple reads, exclusive write

function beginRead(who) {
	if (LOG_MUTEX) log(who + ": Begin read", "info");
	try {
		grabLock("protected", who);
		incrementCounter("activeReaders", who);
		if (getCounter("activeWriters", who) == 0 ) {
			incrementCounter("readingReaders", who);
			signal("readerSem", who);
		}
	}
	finally {
		releaseLock("protected", who);
	}
	waitOn("readerSem", who);
}


function endRead(who) {
	if (LOG_MUTEX) log(who + ": End read", "info");
	try {
		grabLock("protected", who);
		decrementCounter("readingReaders", who);
		decrementCounter("activeReaders", who);
		if (getCounter("readingReaders", who) == 0) {
			while (getCounter("writingWriters", who) < getCounter("activeWriters", who)) {
				incrementCounter("writingWriters", who);
				signal("writerSem", who);
			}
		}
	}
	finally {
		releaseLock("protected", who);
	}
}

function beginWrite(who) {
	if (LOG_MUTEX) log(who + ": Begin write", "info");
	try {
		grabLock("protected", who);
		incrementCounter("activeWriters", who);
		if (getCounter("readingReaders", who) == 0) {
			incrementCounter("writingWriters", who);
			signal("writerSem", who);
		}
	}
	finally {
		releaseLock("protected", who);
	}
	waitOn("writerSem", who);
	grabLock("write", who);
}

function endWrite(who) {
	if (LOG_MUTEX) log(who + ": End write", "info");
	releaseLock("write", who);
	try {
		grabLock("protected", who);
		decrementCounter("writingWriters", who);
		decrementCounter("activeWriters", who);
		if (getCounter("activeWriters", who) == 0) {
			while (getCounter("readingReaders", who) < getCounter("activeReaders", who)) {
				incrementCounter("readingReaders", who);
				signal("readerSem", who);
			}
		}
	}
	finally {
		releaseLock("protected", who);
	}
}
