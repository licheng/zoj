/opt/tomcat/bin/shutdown.sh
rm /opt/tomcat/webapps/NAjudge.war -rf
rm /opt/tomcat/webapps/NAjudge -rf
ant deploy
/opt/tomcat/bin/startup.sh
