#!/bin/bash

if [ -f kludge.patch~ ]; then
	echo "**** Finalizing patch"
	rm kludge.patch~
	find -name "*.orig" | xargs rm
	find -name "*.rej" | xargs rm
	cd target
	diff -Naur groovy-stubs-orig/ groovy-stubs/ >../kludge.patch
else
	echo "**** Starting patch mode"
	mv kludge.patch kludge.patch~
	touch kludge.patch
	mvn clean compile
	cd target
	cp -R groovy-stubs/ groovy-stubs-orig/
	patch -p0 <../kludge.patch~
fi
