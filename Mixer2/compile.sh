#!/bin/bash


javac -d Classes Sources/*/*.java
jar cfe mix2.jar parsing.Parser -C Classes/ .
