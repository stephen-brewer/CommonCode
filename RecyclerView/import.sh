#!/bin/ksh

SRC_DIR=$HOME/Development/androidx-master-dev/frameworks/support/recyclerview/recyclerview/src/main/java
DST_DIR=$HOME/Development/Android/CommonCode/RecyclerView/src/main/java
SRC_PKG_DIR=androidx/recyclerview/widget
DST_PKG_DIR=org/stephenbrewer/arch/recyclerview
SRC_PKG=androidx.recyclerview.widget
DST_PKG=org.stephenbrewer.arch.recyclerview

rm -r $DST_DIR
mkdir -p $DST_DIR/$DST_PKG_DIR
cp -r $SRC_DIR/$SRC_PKG_DIR/* $DST_DIR/$DST_PKG_DIR

cd $DST_DIR/$DST_PKG_DIR
sed -i '' -e 's/package androidx.recyclerview.widget/package org.stephenbrewer.arch.recyclerview/g' *.*
sed -i '' -e 's/import androidx.recyclerview.widget/import org.stephenbrewer.arch.recyclerview/g' *.*
sed -i '' -e 's/import static androidx.recyclerview.widget/import static org.stephenbrewer.arch.recyclerview/g' *.*
ls