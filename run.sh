#!/bin/bash

version="1.0-SNAPSHOT"
input=$1

java -jar ./target/counting-lang-${version}-jar-with-dependencies.jar ${input}
