#!/bin/bash

grep -R -L "Copyright" .. | grep -v "target" | grep -E ".*\.java" | grep -v "src/test/"