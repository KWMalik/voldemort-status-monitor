Voldemort Status Monitor
=======================

Project Voldemort (http://project-voldemort.com/) is a distributed key-value storage system.
At present, it does not seem to have a tool through which Data Centres can monitor status
of nodes of a cluster.

This program provides a simple way to add that a capabilty to your Data centre.
It provides a HTTP GET URL that can be visited in browser to check whether various members
of the Voldemort cluster are up or down.

The program is takes two command line arguments:

1. HTTP Port number.
2. A Java properties file that lists bootstrap URLs of Voldemort Cluster(s) that are to be monitored.

Example Entries:
cluster.1 = tcp://1.2.3.4:7001/
cluster.2 = tcp://1.2.3.5:7001/
cluster.3 = tcp://1.2.3.6:7001/


Sample WebPage:
<h2>Voldemort Cluster 'cluster.2' Status </h2>
<h4>(tcp://localhost:7207/)</h4>
<div><span>localhost:7205 : </span><span style="color:green">NORMAL_SERVER</span></div>
<div><span>localhost:7207 : </span><span style="color:green">NORMAL_SERVER</span></div>
<h2>Voldemort Cluster 'cluster.1' Status </h2>
<h4>(tcp://localhost:7205/)</h4>
<div><span>localhost:7205 : </span><span style="color:green">NORMAL_SERVER</span></div>
<div><span>localhost:7207 : </span><span style="color:green">NORMAL_SERVER</span></div>
