FROM jboss/wildfly:20.0.1.Final

# Deploy artefact
ADD ./target/*.war /opt/jboss/wildfly/standalone/deployments/
