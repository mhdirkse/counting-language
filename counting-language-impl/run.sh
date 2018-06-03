#!/bin/bash

version="1.2-SNAPSHOT"
input=$1

java -jar ./target/counting-language-impl-${version}-jar-with-dependencies.jar ${input}
