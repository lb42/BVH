#!/bin/bash


ant -f buildAnn.xml -DfileName=$1 -DsaxonPath="/usr/share/java/saxonb.jar"
