### Disclaimer 
<b>This code repository is no longer being actively managed by the <a target="_blank" rel="noopener noreferrer" href="https://pti.iu.edu/centers/d2i/">Data To Insight Center</a> at Indiana University Bloomington. Neither the center nor its principals assume responsibility for vulnerabilities that the code may have acquired over time.</b>

PDT (People, Data, Things)
============

PDT provides an API to a MongoDB repository of People, Data, Things used by the SEAD publishing services.

Steps to build:
---------------
1) Checkout the PDT repository from Github
~~~
git clone https://github.com/Data-to-Insight-Center/PDT.git
~~~
2) Move the to root directory and execute following command.
~~~
mvn clean install -DskipTests
~~~
This should build all module needed for the PDT service.

Steps to deploy on Tomcat:
--------------------------

1) Copy the following .war files from relevant target directory into TOMCAT_HOME/webapps.
~~~
sead-pdt.war
~~~

2) Fix the MongoDB parameters in following configuration file under webapp.
~~~
sead-pdt/WEB-INF/classes/org/seadpdt/util/default.properties
~~~

3) Add following two configuration files which includes api keys, to sead-pdt/WEB-INF/classes/ folder.

<b>linkedinprovider.properties</b>
~~~
linkedin.api_key=<api_key_for_linkedIn>
~~~
<b>googleplusprovider.properties</b>
~~~
google.api_key=<api_key_for_google>
~~~

4) Start the server.

Now the API should be accessible through the following URL.
~~~
http://host:port/sead-pdt/..
~~~

Full documentation of PDT API is available at https://github.com/Data-to-Insight-Center/PDT/wiki/API-Documentation
