#!/bin/bash


javac *.java
#nom du jar, point d'entrée fichiers à inclure
jar cfe  faireCompTalismane.jar faireCompTalismane faireCompTalismane.class
jar cfe  faireComp.jar faireComp faireComp.class
