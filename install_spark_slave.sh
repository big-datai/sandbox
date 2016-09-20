#!/bin/bash
#This script is for installation of spark slave on a linux - ubuntu

cd ~/
echo "Utilities for linux:"

sudo apt-get clean
sudo apt-get update -y
sudo apt-get install rpm -y
sudo apt-get install ufw -y
sudo apt-get install curl -y
sudo apt-get install wget -y
#make sure linux is running jdk8 and all env are set...

echo "Installing java"

curl -v -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u102-b14/jdk-8u102-linux-x64.rpm > jdk-8u102-linux-x64.rpm
rpm -Uvh jdk-8u102-linux-x64.rpm
# If fails install from ther repo
sudo apt-cache search jdk
#choose openjdk-8-jdk
sudo apt-get install openjdk-8-jdk -y


wget http://d3kbcqa49mib13.cloudfront.net/spark-2.0.0-bin-hadoop2.7.tgz
tar -zxvf spark-2.0.0-bin-hadoop2.7.tgz
mv spark-2.0.0-bin-hadoop2.7 spark
export SPARK_HOME=$(pwd)/spark
export PATH=$SPARK_HOME/bin:$PATH

#Add system env to you shell 
echo 'export SPARK_HOME=$(pwd)/spark'>>.bashrc
echo 'export PATH=$SPARK_HOME/bin:$PATH'>>.bashrc


