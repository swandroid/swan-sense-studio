#!/bin/bash

##
 # Created by Veaceslav Munteanu <veaceslav.munteanu90@gmail.com>
 # This script will delete all files which should not reach the swan repo
##


# Add all the files you want to be removed to this vector
to_Delete=("*.iml" ".DS_Store" "*.apk" "*.ap_" "*.dex" "*.class" "Thumbs.db" "local.properties")

for i in ${to_Delete[@]}; do
	find . -name $i  -print0 | xargs -0 rm -rf
done
