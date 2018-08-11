#!/bin/bash
POM_FILES=$(find ../*/ -name pom.xml | grep -v "target")

for f in ${POM_FILES}; do
	awk -f parentVersion.awk ${f} | grep "<version>" > tmp
	echo "$f: $(cat tmp)"
done
