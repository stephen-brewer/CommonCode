#!/bin/ksh

SRC_DIR=$HOME/Development/androidx-master-dev/frameworks/support/recyclerview/recyclerview
SRC_DIR_CODE=$SRC_DIR/src/main/java
SRC_DIR_RES=$SRC_DIR/res
DST_DIR=$HOME/Development/Android/CommonCode/RecyclerView/src/main
DST_DIR_CODE=$DST_DIR/java
DST_DIR_RES=$DST_DIR/res
SRC_PKG_DIR=androidx/recyclerview/widget
DST_PKG_DIR=org/stephenbrewer/arch/recyclerview
SRC_PKG=androidx.recyclerview.widget
DST_PKG=org.stephenbrewer.arch.recyclerview

rm -r $DST_DIR_CODE
rm -r $DST_DIR_RES
mkdir -p $DST_DIR_CODE/$DST_PKG_DIR
mkdir -p $DST_DIR_RES
cp -r $SRC_DIR_CODE/$SRC_PKG_DIR/* $DST_DIR_CODE/$DST_PKG_DIR
cp -r $SRC_DIR_RES/* $DST_DIR_RES

cd $DST_DIR_CODE/$DST_PKG_DIR
sed -i '' -e 's/package androidx.recyclerview.widget/package org.stephenbrewer.arch.recyclerview/g' *.*
sed -i '' -e 's/import androidx.recyclerview.widget/import org.stephenbrewer.arch.recyclerview/g' *.*
sed -i '' -e 's/import static androidx.recyclerview.widget/import static org.stephenbrewer.arch.recyclerview/g' *.*
