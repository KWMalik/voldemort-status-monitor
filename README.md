Voldemort Status Monitor
=======================

Project Voldemort (http://project-voldemort.com/) is a distributed key-value storage system.
At present, it does not seem to have a tool through which Data Centres can monitor status
of nodes of a cluster.

This program provides a simple way to add that capabilty to your Data centre.
It provides a HTTP GET URL that can be visited in browser to check whether various members
of the Voldemort cluster are up or down.

The program is takes two command line arguments:

* HTTP Port number.
* A Java properties file that lists bootstrap URLs of Voldemort Cluster(s) that are to be monitored.

> Example Entries, specify IP/Host name of all the members of a cluster
> 
> a.cluster=tcp://a.member-1:7001,tcp://a.member2:7001
> b.cluster=tcp://b.member-1:7001,tcp://b.member2:7001


![Sample output] (voldemort-status-monitor/raw/master/sample.png "Sample output")
