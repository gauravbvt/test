// SEMAPHORES
LOG_MUTEX = true;

MUTEX_READ_COUNT_URI = "transient:readCount";

function grabLock(uri, who) {
	if (LOG_MUTEX)log(who + ": Grab " + uri, "info");
	var req=context.createSubRequest("active:lock");
	req.addArgument("operand",uri);
	context.issueSubRequest(req);	
	if (LOG_MUTEX) log(who + ": Grabbed " + uri, "info");
}

function grabReleaseLock(uri, who) {
	if (LOG_MUTEX) log(who + ": Grab & Release " + uri, "info");
	grabLock(uri, who);
	releaseLock(uri, who);
}

function releaseLock(uri, who) {
	if (LOG_MUTEX) log(who + ": Release " + uri, "info");
	var req=context.createSubRequest("active:unlock");
	req.addArgument("operand",uri);
	context.issueSubRequest(req);
	if (LOG_MUTEX) log(who + ": Released " + uri, "info");
}

function incrementMutex(uri, who) {
	var count = 1 + getMutexCount(uri);
	setMutexCount(uri, count);
	if (LOG_MUTEX) log(who + ": Incremented mutex " + uri + " to " + count, "info");
}

function decrementMutex(uri, who) {
	var count = Math.max((getMutexCount(uri) - 1), 0);
	setMutexCount(uri, count);
	if (LOG_MUTEX) log(who + ": Decremented mutex " + uri + " to " + count, "info");
}

function initializeMutex(uri) {
	setMutexCount(uri,0);
}

function initializeReadCountMutex() {
	setMutexCount(MUTEX_READ_COUNT_URI,0);	
}

function setMutexCount(uri, count) {
	if (LOG_MUTEX) log("Setting mutex " + uri + " to " + count, "info");
	context.sinkAspect(uri, new StringAspect(""+count));
}

function getMutexCount(uri) {
	if (LOG_MUTEX) log("Getting mutex " + uri, "info");
	var count = context.sourceAspect(uri, IAspectString).getString();
	if (LOG_MUTEX) log("Got mutex " + uri + " = " + count, "info");
	return parseInt(count);
}

function sleep(msecs) {
	log("Sleeping for " + msecs, "info");
	var req = context.createSubRequest("active:sleep");
	var time = <time>
								{msecs}
							</time>;
	req.addArgument("operator", new XmlObjectAspect(time.getXmlObject()));
	context.issueSubRequest(req);
}


// Locking

// Wait for write lock to be released if grabbed.
// Grab read lock then increment read mutex by one, release read lock.
function beginRead(who) {
	if (LOG_MUTEX) log(who + ": Begin read", "info");
	try {
		grabReleaseLock("lock:write", who); // Can only go through when write lock not already grabbed 
		grabLock("lock:read", who);
		incrementMutex(MUTEX_READ_COUNT_URI, who);
	}
	finally {
		releaseLock("lock:read", who);
	}
}

// Grab read lock, decrement read mutex by one, release read lock
function endRead(who) {
	if (LOG_MUTEX) log(who + ": End read", "info");
	try {
		grabLock("lock:read", who);
		decrementMutex(MUTEX_READ_COUNT_URI, who);
	}
	finally {
		releaseLock("lock:read", who);
	}
}
// Grab write lock to block new read or writes.
// Grab read lock. If read mutex > 0 then release read lock. Try again (after short sleep).
// When read mutex = 0, grab read lock.
// Release write lock, keeping read lock.
function beginWrite(who) {
	if (LOG_MUTEX) log(who + ": Begin write", "info");
	try {
		grabLock("lock:write", who);
		var done = false;
		do {
			var count;
			try {
				grabLock("lock:read", who);
				count = getMutexCount(MUTEX_READ_COUNT_URI, who);
			}
			catch (e) {
				releaseLock("lock:read", who);
				throw e;
			}
			if (count == 0) {
					done = true;
			}
			else {
				releaseLock("lock:read", who);
				sleep(100);
			}
		} while (!done);
	}
	finally {
		releaseLock("lock:write", who);
	}
}

// Release read lock.
function endWrite(who) {
	if (LOG_MUTEX) log(who + ": End write", "info");
	releaseLock("lock:read", who);
}
