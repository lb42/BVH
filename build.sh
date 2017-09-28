#!/bin/bash
php ../Dramagraph/Base.php moliere.sqlite urls_moliere.txt
php ../Dramagraph/Base.php contexte.sqlite urls_contexte.txt
php ../Teinte/Build.php
