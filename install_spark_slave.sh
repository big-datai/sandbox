#!/bin/bash
#This script is for installation of spark slave on a linux - ubuntu
#It updates system install utils, jdk 8 and spark 2.0

# MUST - set up innner etch0:0 ips to the server
# MUST - run ufw allow script

sudo vim /etc/network/interfaces
# To add a private IP address:
auto eth0:0
iface eth0:0 inet static
    address 192.168.218.83
    netmask 255.255.128.0

sudo ifup eth0:0

cd ~/
echo "Utilities for linux:"

sudo apt-get clean
sudo apt-get update -y
sudo apt-get install rpm -y
sudo apt-get install ufw -y
sudo apt-get install curl -y
sudo apt-get install wget -y
sudo apt-get install git -y
sudo apt-get install vim -y
sudo apt-get install cron -y
sudo apt-get install htop -y
# install anacona follow the instructions.
wget https://repo.continuum.io/archive/Anaconda3-4.2.0-Linux-x86_64.sh
bash Anaconda3-4.2.0-Linux-x86_64.sh 

git clone https://github.com/2dmitrypavlov/sandbox.git
#make sure linux is running jdk8 and all env are set...

echo "Installing java"

#curl -v -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u102-b14/jdk-8u102-linux-x64.rpm > jdk-8u102-linux-x64.rpm
#rpm -Uvh jdk-8u102-linux-x64.rpm
# If fails install from ther repo
#sudo apt-cache search jdk
#choose openjdk-8-jdk
sudo apt-get install oracle-java8-installer -y


wget http://d3kbcqa49mib13.cloudfront.net/spark-2.0.1-bin-hadoop2.7.tgz
tar -zxvf spark-2.0.1-bin-hadoop2.7.tgz
mv spark-2.0.1-bin-hadoop2.7 spark
export SPARK_HOME=$(pwd)/spark
export PATH=$SPARK_HOME/bin:$PATH
export MASTER_IP=192.168.131.44
#Add system env to you shell 
echo 'export SPARK_HOME='$(pwd)'/spark'>>.bashrc
echo 'export PATH=$SPARK_HOME/bin:$PATH'>>.bashrc
echo 'export MASTER_IP='$MASTER_IP>>.bashrc

#Here you need to allow your firewall all inner network connections
#---------------------------------- FIREWALL START--------------------------------------
#use ufw for iptables managment
#sudo iptables -F
sudo ufw status numbered
sudo ufw enable
sudo ufw status numbered
sudo ufw allow 22
# allow access from you subnet, substitude this example ip with real
# don't forget to allow local it on the master machine to allow it managing and accessing the slave

# for example this one allows all subnetwork of kind 192.168.0.0 - 192.168.255.255
sudo ufw allow from 192.168.0.0/16 to any
# list of all servers in spark cluster must run on each machine
sudo ufw allow from 192.168.0.0/16 to any
sudo ufw allow from 192.168.0.0/16 to any
sudo ufw allow from 192.168.0.0/16 to any
sudo ufw allow from 192.168.0.0/16 to any
sudo ufw allow from 192.168.0.0/16 to any
sudo ufw allow from 192.168.0.0/16 to any

#Allow connections to a specific network interface
#sudo ufw allow in on eth1
#---------------------------------- FIREWALL END --------------------------------------

# installing ganglia deamon 
sudo apt-get install ganglia-monitor -y
sudo cp -a ~/sandbox/gmond.conf /etc/ganglia/
sudo service ganglia-monitor restart

#add start slave script to cron - if you want your slave start when servers are up
#sudo crontab -e
sudo echo 'SPARK_HOME='$(pwd)'/spark'>>/etc/crontab
sudo echo 'PATH='$PATH>>/etc/crontab
sudo echo 'MASTER_IP='$MASTER_IP>>/etc/crontab
sudo echo '@reboot    '$(whoami)'    $SPARK_HOME/sbin/start-slave.sh spark://$MASTER_IP:7077' >>/etc/crontab

# if you are not running spark on this machine turn of ganglia and restart gmeta on master machine
#sudo service ganglia-monitor stop
# or instead add rule in crone that turn on/of ganglia if spark is running

