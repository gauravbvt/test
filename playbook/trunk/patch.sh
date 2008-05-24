#!/bin/bash

if [ -f kludge.patch~ ]; then
	echo "**** Finalizing patch"
	rm kludge.patch~
	cd target
	find -name "*.orig" -exec rm \{\} \; 
	find -name "*.rej"  -exec rm \{\} \;
	find -name "*~"  -exec rm \{\} \;
	diff -Naur groovy-stubs-orig/ groovy-stubs/ >../kludge.patch
else
	echo "**** Starting patch mode"
	mv kludge.patch kludge.patch~
	touch kludge.patch
	mvn -o clean compile
	cd target
	cp -R groovy-stubs/ groovy-stubs-orig/
	patch -p0 <../kludge.patch~
fi
