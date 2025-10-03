# Use Tomcat as the base image
FROM tomcat:10.1-jdk21

# Remove the default ROOT webapp
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your prebuilt WAR file from repo into Tomcat
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
