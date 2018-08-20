#!groovy

def mvn(String command) {
    // Get the maven tool.
    // ** NOTE: This 'Maven 3.3.3' maven tool must be configured
    // **       in the global configuration.
    //def mvnHome = tool 'Maven 3.3.3'
    def mvnHome
    def oracleJdkTool
    if (env.TINI_SHA == '6c41ec7d33e857d4779f14d9c74924cab0c7973485d2972419a3b7c7620ff5fd') {
        // Setup tooling Jenkins docker
        oracleJdkTool = tool name: 'jdk', type: 'hudson.model.JDK'
        mvnHome = tool 'maven'
    } else {
        // Setup tooling Jenkins2 Rabobank
        oracleJdkTool = tool name: 'jdk1.8.0.45', type: 'hudson.model.JDK'
        mvnHome = tool 'maven-3.3.3'


    }
    //def mvnHome = '/opt/apache-maven-3.3.3'

    configFileProvider([configFile(fileId: 'dfb821b9-3323-44d9-a614-543cdf3849f3', variable: 'MAVEN_SETTINGS')]) {
        sh "${mvnHome}/bin/mvn ${command} -s ${MAVEN_SETTINGS}"
    }
}

def captureTestResults(String location) {
    step([$class: 'JUnitResultArchiver', testResults: "**/target/${location}/TEST-*.xml"])
}

def exec(String script) {
    return sh(script: script, returnStdout: true).trim()
}

def sonar(boolean previewOnly, String extraMvnCommand, String extraSonarReportLocation) {
    def oracleJdkTool
    if (env.TINI_SHA == '6c41ec7d33e857d4779f14d9c74924cab0c7973485d2972419a3b7c7620ff5fd') {
        // Setup tooling Jenkins docker
        oracleJdkTool = tool name: 'jdk', type: 'hudson.model.JDK'
    } else {
        // Setup tooling Jenkins2 Rabobank
        oracleJdkTool = tool name: 'jdk1.8.0.45', type: 'hudson.model.JDK'
    }
    // Run sonar checks, with Oracle JVM
    def mvnArgs = extraMvnCommand+'sonar:sonar -Dsonar.host.url=http://sonar.rabobank.nl:9000'
    if (previewOnly) {
        mvnArgs += ' -Dsonar.analysis.mode=preview -Dsonar.issuesReport.html.enable=true'
    }

    // get Oracle JDK (for use with Sonar)
    //def oracleJdkTool = tool name: 'jdk1.8.0.45', type: 'hudson.model.JDK'
    echo "Using Oracle's JAVA_HOME: ${oracleJdkTool}"
    withEnv(["JAVA_HOME=${oracleJdkTool}", 'MAVEN_OPTS=-XX:MaxPermSize=96m']) {
        mvn(mvnArgs)
    }

    if (previewOnly) {
        // publish results
        publishHTML(target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
                             reportDir: extraSonarReportLocation+'target/sonar/issues-report', reportFiles: 'issues-report.html',
                             reportName: 'Sonar Report'])
    }
}

return this;
