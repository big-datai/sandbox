#!/bin/bash
#install R for spark

#to install R version do next:

sudo vim /etc/apt/sources.list

#add: in the end of the file

deb http://cran.r-project.org/bin/linux/ubuntu precise/
deb https://cran.r-project.org/bin/linux/ubuntu trusty/

sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9
sudo apt-get update

#check the versions of R we have
sudo apt-cache madison r-base-dev
# install packages
sudo apt-get -f install r-base-core=3.2.3-4trusty0 
sudo apt-get install r-base-dev=3.2.3-4trusty0
sudo apt-get install r-base-html=3.2.3-6trusty0
sudo apt-get install r-recommended=3.2.3-6trusty0
sudo apt-get install r-base=3.2.3-6trusty0



#Installin the r-kernel:(https://github.com/IRkernel/IRkernel)
# run R
R
#run following in R
install.packages(c('repr', 'IRdisplay', 'crayon', 'pbdZMQ', 'devtools'))
devtools::install_github('IRkernel/IRkernel')
IRkernel::installspec() 
IRkernel::installspec(name = 'ir32', displayname = 'R 3.2')
