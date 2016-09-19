#!/bin/bash
#This script is for installation of spark slave on a linux - ubuntu

echo "Utilities for linux:"

sudo apt-get clean
sudo apt-get update
sudo apt-get install rpm
#make sure linux is running jdk8 and all env are set...

echo "Installing java"

curl -v -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u102-b14/jdk-8u102-linux-x64.rpm > jdk-8u102-linux-x64.rpm
rpm -Uvh jdk-8u102-linux-x64.rpm


