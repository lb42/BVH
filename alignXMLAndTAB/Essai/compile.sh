#!/bin/bash

javac -d Classes Sources/*.java
jar cfe alignXMLAndTab.jar alignXMLAndTab -C Classes/ .
