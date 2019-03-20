#!/bin/bash


javac -d Classes SourcesBis/*/*.java
jar cfe mix2.jar parsing.Parser -C Classes/ .
