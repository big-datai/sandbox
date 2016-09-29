#!/bin/bash

#updating your OS system

sudo yum update -y
sudo apt-get update -y
wget http://d3kbcqa49mib13.cloudfront.net/spark-2.0.0-bin-hadoop2.7.tgz
tar -zxvf spark-2.0.0-bin-hadoop2.7.tgz
mv spark-2.0.0-bin-hadoop2.7 spark
export SPARK_HOME=$(pwd)/spark
export PATH=$SPARK_HOME/bin:$PATH
export MASTER_IP=$(hostname -i)
export SPARK_MASTER=spark://$(hostname -i):7077

curl -v -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u102-b14/jdk-8u102-linux-x64.rpm > jdk-8u102-linux-x64.rpm
rpm -Uvh jdk-8u102-linux-x64.rpm

#Setting up zeppelin
wget http://apache.spd.co.il/zeppelin/zeppelin-0.6.1/zeppelin-0.6.1-bin-all.tgz
tar -zxvf zeppelin-0.6.1-bin-all.tgz
mv zeppelin-0.6.1-bin-all zeppelin
cd zeppelin/conf
cp zeppelin-env.sh.template zeppelin-env.sh
echo 'export MASTER=spark://'$(hostname -i)':7077'>>zeppelin-env.sh
echo 'export ZEPPELIN_NOTEBOOK_STORAGE="org.apache.zeppelin.notebook.repo.VFSNotebookRepo, org.apache.zeppelin.notebook.repo.zeppelinhub.ZeppelinHubRepo"'>>zeppelin-env.sh
echo 'export ZEPPELINHUB_API_ADDRESS="https://www.zeppelinhub.com"'>>zeppelin-env.sh
echo 'export ZEPPELIN_PORT=9995'>>zeppelin-env.sh
echo 'export ZEPPELINHUB_API_TOKEN="'"1f37d450-c78a-4a4f-ab75-1501f47bc246"'"'>>zeppelin-env.sh

sed -i  "s|#/\*\* = authc|/\*\* = authc|" shiro.ini
sed -i "s|/\*\* = anon|#/\*\* = anon|" shiro.ini
sed -i "s|admin = password1|admin = vCugoi%8yg6#ujk9@|" shiro.ini
cd ../bin/
./zeppelin-daemon.sh restart


#start spark
cd $SPARK_HOME/sbin
./start-master.sh --ip $(hostname)
./start-slave.sh $SPARK_MASTER
#./start-slave.sh spark://localhost:7077

echo 'export SPARK_HOME='$(pwd)'/spark/'>> ~/.bashrc
echo 'export PATH='$SPARK_HOME'/bin:'$PATH''>> ~/.bashrc
echo 'export MASTER=spark://'$(hostname)':7077'>>~/.bashrc
echo 'export MASTER_IP='$MASTER_IP>>~/.bashrc





