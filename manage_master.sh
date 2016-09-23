#!/bin/bash

#start master

$SPARK_HOME/sbin/start-master.sh --ip $(hostname -i)



#start slave


$SPARK_HOME/sbin/start-slave.sh spark://$MASTER_IP:7077

#start ganglia
