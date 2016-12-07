#!/bin/bash
#Setting up zeppelin
wget http://www-eu.apache.org/dist/zeppelin/zeppelin-0.6.2/zeppelin-0.6.2-bin-all.tgz
tar -zxvf zeppelin-0.6.2-bin-all.tgz
mv zeppelin-0.6.2-bin-all zeppelin
cd zeppelin/conf
cp zeppelin-env.sh.template zeppelin-env.sh
echo 'export MASTER=spark://sparkm2:7077'>>zeppelin-env.sh
#echo 'export ZEPPELIN_NOTEBOOK_STORAGE="org.apache.zeppelin.notebook.repo.VFSNotebookRepo, org.apache.zeppelin.notebook.repo.zeppelinhub.ZeppelinHubRepo"'>>zeppelin-env.sh
#echo 'export ZEPPELINHUB_API_ADDRESS="https://www.zeppelinhub.com"'>>zeppelin-env.sh
echo 'export ZEPPELIN_PORT=9995'>>zeppelin-env.sh
#echo 'export ZEPPELINHUB_API_TOKEN="'"1f37d450-c78a-4a4f-ab75-1501f47bc246"'"'>>zeppelin-env.sh
echo 'export SPARK_SUBMIT_OPTIONS="--jars /home/rifiniti/mysql-connector-java-5.1.39.jar,/home/rifiniti/aws-java-sdk-1.7.4.jar,/home/rifiniti/hadoop-aws-2.6.0.jar"'>>zeppelin-env.sh
sed -i  "s|#/\*\* = authc|/\*\* = authc|" shiro.ini
sed -i "s|/\*\* = anon|#/\*\* = anon|" shiro.ini
sed -i "s|admin = password1|admin = vCugoi%8yg6#ujk9@|" shiro.ini
cd ../bin/
./zeppelin-daemon.sh restart
