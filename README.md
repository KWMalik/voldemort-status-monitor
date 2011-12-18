Voldemort Status Monitor
=======================

Project Voldemort (http://project-voldemort.com/) is a distributed key-value storage system.
At present, it does not seem to have a tool through which Data Centres can monitor status
of nodes of a cluster.

This program provides a simple way to add that capabilty to your Data centre.
It provides a HTTP GET URL that can be visited in browser to check whether various members
of the Voldemort cluster are up or down.

The program is takes two command line arguments:

1.HTTP Port number.
2.A Java properties file that lists bootstrap URLs of Voldemort Cluster(s) that are to be monitored.

Example Entries:
> cluster.1 = tcp://1.2.3.4:7001/
> cluster.2 = tcp://1.2.3.5:7001/
> cluster.3 = tcp://1.2.3.6:7001/

<img src="voldemort-status-monitor/blob/master/sample.png" />