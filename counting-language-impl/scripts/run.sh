#!/bin/bash

input=$1

java -jar ../counting-language-impl-${project.version}-jar-with-dependencies.jar ${input}
