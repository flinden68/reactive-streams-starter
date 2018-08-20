#!groovy

@Library('RISIT4IT') _
@Library('RISROCS') __

node {
    def APP_NAME = "relevance-profile-capture-service"

    CF_API_TEST = 'https://api.sys.pcf-t01-we.rabobank.nl'
    CF_DOMAIN_PROD = 'apps.pcf-p01.rabobank.nl'
    CF_ORG = 'ris-org'
    CF_SPACE = 'nutella-space'
    CREDENTIALS_ID = 'pcf-nutella'
    VERSION = "1.0.${env.BUILD_NUMBER}"

    def JENKINS_SSH_HASH = '7069a906-eb0e-44f0-95b7-4ddb6a65f041'
    def REMOTE_HOST = 'lsrv6303.linux.rabobank.nl'
    def REMOTE_PORT = 2375
    def DOCKER_HOST = REMOTE_HOST + ':' + REMOTE_PORT
    def RELEASE_VERSION

    OPERATORS_LIST_JENKINS = ["lindenf", "duvans", "holleman", "stofkoc", "josephk"]
    OPERATORS_LIST_SM9 = ["lindenf", "ameszxx"]
    OPERATORS_LIST_MAIL = ["Frank.van.der.Linden02@rabobank.nl", "Savas.Duvan@rabobank.nl", "Xander.Amesz@rabobank.nl", "Maikel.Holleman@rabobank.nl", "Chai.Stofkoper@rabobank.nl", "Kurt.Joseph@rabobank.nl"]

    def jdk
    def mvnHome
    def mvnSettings

    def m = [POM                        : "."
             , SONAR                    : true
             , BREAK_ON_SONAR           : true
             , DEPLOY                   : false
             , TEAM_SERVER              : 's6117'
             , MAVEN_BUILD_OPTIONS      : ""
             , CLM                      : false
             , BREAK_ON_CLM             : false
             , FORTIFY                  : false
             , BREAK_ON_FORTIFY         : false
             , LST                      : true
             , BREAK_ON_LST             : true
             , TESTS                    : true
             , RELEASE_BRANCH           : 'release'
             , DEVELOP_BRANCH           : 'develop'
             , LST_BRANCH               : 'performance-test'
             , MASTER_BRANCH            : 'master'
             , FOLDERS_TO_STORE         : [:]
             , ADDITIONAL_MAVEN_STEPS   : [:]
             , ENVIRONMENT_TAG          : 'latest'
             , SYSTEM_TEST_MAVEN_COMMAND: ''
             , RUN_SYSTEM_TEST          : false
             , RUN_E2E_TEST             : false
             , E2E_CONF                 : 'lsrv6303.conf.js'
             , PAUSE_FOR_SYSTEM_TESTS   : 60
             , 'develop'                : [DEPLOY: false, PUBLISH: false, DEPLOYF: true, PUBLISHF: false]
             , 'release'                : [DEPLOY: false, PUBLISH: false, DEPLOYF: true, PUBLISHF: false]]
    def develop = env.BRANCH_NAME == m.DEVELOP_BRANCH
    def release = env.BRANCH_NAME == m.RELEASE_BRANCH
    def master = env.BRANCH_NAME == m.MASTER_BRANCH
    def performancetest = env.BRANCH_NAME == m.LST_BRANCH
    def systemError = false;
    def skip = false;

    stage('Cleanup workspace') {
        step([$class: 'WsCleanup', deleteDirs: true, notFailBuild: true, patterns: [[pattern: '*', type: 'INCLUDE']]])
    }

    stage('Checkout source') {
        checkout scm
    }

    def version
    def groupId
    def name
    def helper = load 'build-helper.groovy'

    if (env.TINI_SHA == '6c41ec7d33e857d4779f14d9c74924cab0c7973485d2972419a3b7c7620ff5fd') {
        configFileProvider([configFile(fileId: 'nutella_maven_settings', targetLocation: 'settings.xml')]) {}
        properties([pipelineTriggers([[$class: 'BitBucketTrigger'], pollSCM('H/2 * * * *')])])
        jdk = tool name: 'jdk', type: 'hudson.model.JDK'
        def pominfo = readMavenPom file: 'service/pom.xml'
        version = pominfo.version
        groupId = pominfo.parent.groupId
        artifactId = pominfo.artifactId
    } else {
        jdk = tool name: 'jdk1.8.0_60', type: 'hudson.model.JDK'
        version = '1.0.0'
    }
    env.JAVA_HOME = "${jdk}"
    env.JMETER_HOME = "/var/tmp/apache-jmeter-3.3"

    def hostname = sh(script: 'hostname', returnStdout: true).trim()

    stage('Build') {
        println 'Going for build in ' + env.WORKSPACE
        if (master) {
            error 'Trying to build the master!!'
            skip = true
        }

        if (!skip) {
            println 'Not skipping' + env.WORKSPACE
            timeout(time: 20, unit: 'MINUTES') {
                println "Branch name: ${env.BRANCH_NAME}"
                println "Branch name: ${env.JAVA_HOME}"
                println "Group id: ${groupId}"
                println "Artifact id: ${artifactId}"
                println "Build version: ${version}"
                println "Build number: ${currentBuild.number}"
                println "Building ..."
                maven "-f service/pom.xml -U clean package -DskipTests"
            }
        }
    }

    stage('Test') {
        systemError |= helper.ignoringErrors {
            println "Testing ..."
            maven "test -f service/pom.xml"
            junit '**/target/surefire-reports/TEST-*.xml'
        }
    }

    def codeQualityStages = [:]
    if (m.SONAR) {
        codeQualityStages['SONAR'] = {
            def parameters = [:]
            parameters.put('POM', '.')
            parameters.put('SONAR_HOST', 'http://sonar.rabobank.nl:9000')
            parameters.put('skip', false)
            parameters.put('BREAK_ON_SONAR', m.BREAK_ON_SONAR)
            parameters.put('PUBLISH_REPORT', true)
            parameters.put('REPORT_LOCATION', 'service/')
            parameters.put('EXTRA_MVN_COMMAND', '-f service/pom.xml ')
            mavenSonarAnalysis(parameters)
        }
    }
    if (m.CLM) {
        codeQualityStages['CLM'] = {
            println "Not implemented yet"
        }
    }
    if (m.FORTIFY) {
        codeQualityStages['FORTIFY'] = {
            println "Not implemented yet"
        }
    }

    if (codeQualityStages.size() > 0) {
        systemError |= helper.ignoringErrors {
            stage('Code Quality') {
                if (!systemError) {
                    parallel codeQualityStages
                }
            }
        }
    }

    if (performancetest && m.LST) {
        stage("Load test") {
            systemError |= helper.ignoringErrors {
                try {

                    echo "Do the Load Test"
                    sh("/var/tmp/apache-jmeter-3.3/bin/jmeter.sh -n -t 'functional-testing/src/test/jmeter/pipeline-relevance-profile-capture.jmx' -l 'functional-testing/src/test/jmeter/relevance-profile.capture.jtl' -e -o 'functional-testing/src/test/jmeter/reports/'")
                    sh("mkdir -p target/jmeter_results/")
                    sh("cp -r functional-testing/src/test/jmeter/reports/. target/jmeter_results/")
                    sh("cp -r functional-testing/src/test/jmeter/relevance-profile.capture.jtl target/jmeter_results/")

                    publishHTML([allowMissing         : false,
                                 alwaysLinkToLastBuild: true,
                                 keepAll              : true,
                                 reportDir            : 'target/jmeter_results',
                                 reportFiles          : 'index.html',
                                 reportName           : 'JMeter Report'
                    ])

                    performanceReport parsers: [[$class: 'JMeterParser', glob: "target/jmeter_results/relevance-profile.capture.jtl"]], errorFailedThreshold: 10, errorUnstableThreshold: 10, ignoreFailedBuild: false, ignoreUnstableBuild: false, relativeFailedThresholdNegative: 1.2, relativeFailedThresholdPositive: 1.89, relativeUnstableThresholdNegative: 1.8, relativeUnstableThresholdPositive: 1.5


                } catch (exc) {
                    // if any exception occurs, mark the build as failed
                    currentBuild.result = 'FAILURE'
                    throw exc
                }
            }
        }
        /*println "LET START DOCKER STAGES:"
        String docker = "docker -H tcp://${DOCKER_HOST}"

        def imageId
        stage('Build Docker') {
            sh "(cd service/docker && sh prepareComponents.sh)"
            imageId = helper.exec("${docker} build -t nutella/relevance-profile-capture-service service/docker/components | awk '/Successfully built/ {print \$3}'")
        }

        def appId

        stage('Start Docker') {
            systemError |= helper.ignoringErrors {
                appId = dockerStartContainer(imageId, docker, REMOTE_HOST)
            }
        }
        if (appId != null) {
            stage("Load test") {
                systemError |= helper.ignoringErrors {
                    try {

                        echo "Do the Load Test"
                        sh("/var/tmp/apache-jmeter-3.3/bin/jmeter.sh -n -t 'functional-testing/src/test/jmeter/pipeline-relevance-profile-capture.jmx' -l 'functional-testing/src/test/jmeter/relevance-profile.capture.jtl' -e -o 'functional-testing/src/test/jmeter/reports/'")
                        sh ("mkdir -p target/jmeter_results/")
                        sh ("cp -r functional-testing/src/test/jmeter/reports/. target/jmeter_results/")
                        sh ("cp -r functional-testing/src/test/jmeter/relevance-profile.capture.jtl target/jmeter_results/")

                        publishHTML([allowMissing: false,
                                     alwaysLinkToLastBuild: true,
                                     keepAll: true,
                                     reportDir: 'target/jmeter_results',
                                     reportFiles: 'index.html',
                                     reportName: 'JMeter Report'
                        ])

                        performanceReport parsers: [[$class: 'JMeterParser', glob: "target/jmeter_results/relevance-profile.capture.jtl"]], errorFailedThreshold: 10, errorUnstableThreshold: 10, ignoreFailedBuild: false, ignoreUnstableBuild: false, relativeFailedThresholdNegative: 1.2, relativeFailedThresholdPositive: 1.89, relativeUnstableThresholdNegative: 1.8, relativeUnstableThresholdPositive: 1.5


                    } catch (exc) {
                        // if any exception occurs, mark the build as failed
                        currentBuild.result = 'FAILURE'
                        throw exc
                    }
                }
            }
            stage('Stop Docker') {
                systemError |= helper.ignoringErrors {
                    dockerRemoveContainer(appId, imageId, docker)
                }
            }
        }*/
    }

    if (systemError) {
        currentBuild.result = "UNSTABLE"
    } else {
        if (develop || release) {
            stage('Deploy') {

                if (develop) {
                    withCloudFoundry(CF_API_TEST, CF_ORG, CF_SPACE, CREDENTIALS_ID, {
                        parallel(
                                "${APP_NAME}": {
                                    parallel(
                                            "${APP_NAME}-app-autoscaler": {
                                                createCloudService([
                                                        registryServiceName: "app-autoscaler",
                                                        plan               : "standard",
                                                        serviceName        : "app-autoscaler"])
                                            }
                                    )
                                    sh "cf push"
                                }
                        )
                    })
                }

                if (release) {
                    stage('Release') {
                        //initiateFourEyesPrinciple()
                        //mavenReleaseArtifact()
                        //updateJarWithCertificates('service/target/relevance-profile-capture-service.jar', 'prod')
                    }

                    /*stage('Start SM9 change process') {
                        def changeParameters = [
                                //the title of the change
                                title                   : "$APP_NAME - ${VERSION}",
                                //Array of operators that are allowed to interact with this pipeline
                                allowedJenkinsOperators : OPERATORS_LIST_JENKINS,
                                //Operators/Coordinators in SM9 that can be used in the change
                                allowedSM9OperatorsList : OPERATORS_LIST_SM9,
                                allowedMailOperatorsList: OPERATORS_LIST_MAIL,
                                category                : "Normal Change",
                                subCategory             : "DV ONLINE",
                                //The change model in SM9 that will be used for this change registration
                                model                   : "Algemene wijziging binnen DV online",
                                configurationItems      : ["Profile Capture Service"],
                                //URL with more info about the change
                                changeUrl               : "${env.BUILD_URL}changes",
                                impact                  : 5,
                                urgency                 : 4,
                                riskAssessment          : 3,
                                documentation           : ["https://confluence.dev.rabobank.nl/x/9V6ABQ"],
                                dependencies            : ["None"],
                                importantNotices        : ["None"],
                                aftercare               : ["None"],
                                *//*jsonFileName            : "/testresults/PIF/Microservices/edge_router_service/edge-router-service-service-change.json",*//*
                                hostname                : hostname
                        ]
                        //Create the change object. It will also ask for input to make the change.
                        change = createSM9Change(changeParameters)
                        updateSM9ChangeProceedToImplementation(change)
                        updateSM9ChangeStartImplementation(change)
                    }*/

                    //deployToProduction(APP_NAME)

                    /*stage('Close SM9 change process') {
                        closeSM9Change(change)
                    }*/

                }


            }
            if (systemError) {
                error "Build failed"
            }
        }
    }

    stage('Deploy Frontend') {
        def pominfoFrontend = readMavenPom file: 'frontend/pom.xml'
        def versionFrontend = pominfoFrontend.version
        def artifactIdFrontend = pominfoFrontend.artifactId
        def groupIdFrontend = pominfoFrontend.groupId

        if (develop) {

            if (m.'develop'.DEPLOYF) {
                maven "-f frontend/pom.xml deploy -DskipTests  -Denv=dev"
                println "Develop Frontend artifact deployed to nexus snapshot repository"
            } else {
                println "Deploy Frontend to Nexus skipped"
            }
            if (m.'develop'.PUBLISHF) {
                def parameters = [:]
                parameters.put('POM', 'frontend/')
                parameters.put('CLASSLOADER_MODE', 'PARENT_LAST')
                parameters.put('CLASSLOADER_MODULES_MODE', 'PARENT_LAST')
                parameters.put('STAGE', 't')
                parameters.put('skip', false)
                parameters.put('DEPLOY_TO_SERVERS', m.TEAM_SERVER)
                parameters.put('MODULE_TO_DEPLOY', "${artifactIdFrontend}")
                parameters.put('VERSION', "${versionFrontend}")
                parameters.put('GROUP_ID', "${groupIdFrontend}")
                println "Deploy parameters develop: ${parameters}"
                mavenPicomaDeployment(parameters)
                println "Develop Frontend artifact published to Picoma"
            } else {
                println "Frontend Publish to Picoma skipped"
            }

        }

        if (release) {
            systemError |= helper.ignoringErrors {
                if (m.'release'.DEPLOYF) {
                    maven "-f frontend/pom.xml deploy -DskipTests -Denv=prod"
                    println "Release Frontend artifact deployed to nexus releases repository"
                } else {
                    println "Deploy Frontend to Nexus skipped"
                }
                if (m.'release'.PUBLISHF) {
                    def parameters = [:]
                    parameters.put('POM', 'frontend/')
                    parameters.put('CLASSLOADER_MODE', 'PARENT_LAST')
                    parameters.put('CLASSLOADER_MODULES_MODE', 'PARENT_LAST')
                    parameters.put('STAGE', 't')
                    parameters.put('skip', false)
                    parameters.put('DEPLOY_TO_SERVERS', m.TEAM_SERVER)
                    parameters.put('MODULE_TO_DEPLOY', "${artifactIdFrontend}")
                    parameters.put('VERSION', "${versionFrontend}")
                    parameters.put('GROUP_ID', "${groupIdFrontend}")
                    println "Deploy parameters release: ${parameters}"
                    mavenPicomaDeployment(parameters)
                    println "Release Frontend artifact published to Picoma"
                } else {
                    println "Frontend Publish to Picoma skipped"
                }
            }
        }
    }
}

def lockBuild(String step, Closure body) {
    lock(resource: step, inversePrecedence: true) {
        try {
            body.call()
        } finally {
            milestone()
        }
    }
}

def executeWithNotification(String errorMessage, Closure body) {
    try {
        body.call()
    } catch (err) {
        //notifyFailure(errorMessage)
        throw err
    }
}

def mvn(String phase) {
    maven " -U ${phase} -Djavax.xml.accessExternalSchema=all"
}

def mavenReleaseArtifact() {
// below config files are needed to release the project
    configFileProvider([configFile(fileId: '20082155-7ca4-4aee-beba-ae2d98acca3f', targetLocation: 'settings.xml'),
                        configFile(fileId: '7cfc086f-b4b5-4658-b1eb-92f2cd9416b1', targetLocation: 'git/credentials')]) {
        // Required for Git to find the credentials location
        env.XDG_CONFIG_HOME = "${WORKSPACE}"
        mvn "-B -DreleaseVersion=${VERSION} release:prepare release:perform -Darguments='-DskipTests'"
    }
    try {
        // Push changes back to develop and set to the released version for the rest of the build
        sh 'git fetch && git push origin HEAD:develop && git checkout HEAD^'
    } catch (ignored) {
        echo 'Merging master back into develop has failed. Do this manually.'
        unstableBuild = true
    }
}

// we need to update the JAR to include the Rabobank certificates, this has to be done till we start using the config server
def updateJarWithCertificates(String jarLocation, String environment) {
    String tempDir = "/tmp/${env.JOB_NAME}-${env.BUILD_NUMBER}-${environment}"
    String springBootClasspath = "BOOT-INF/classes"
    sh "mkdir -p ${tempDir}/${springBootClasspath} && cp -rf config/edge-router/certs/${environment}/*.jks ${tempDir}/${springBootClasspath}"
    if (environment == "prod") {
        sh "cp -rf config/edge-router/jws-keys/${environment}/private_rsa_key ${tempDir}/${springBootClasspath}"
    }
    sh "jar uf ${jarLocation} -C ${tempDir} ."
    sh "rm -rf ${tempDir}"
}

// Test reports
def publishJunitResults() {
    junit allowEmptyResults: true, testResults: "**/target/*-reports/TEST*.xml"
}

// 4-Eye Principle
def initiateFourEyesPrinciple() {
    timeout(time: 1, unit: 'DAYS') {
        //sendHipChatNotificationMessage('YELLOW', "Awaiting two (2) approvals before going to production", "")

        def operators = ["ameszxx", "lindenf"]

        def approver1 = input(submitterParameter: 'approver', submitter: operators.join(","))
        operators.remove(approver1)
        def approver2 = input(submitterParameter: 'approver', submitter: operators.join(","))

        //sendHipChatNotificationMessage('GREEN', "Deploying to production! Approved by ${approver1} and ${approver2}", "")
    }
}

def createServiceKey(String serviceName, String keyName) {
    sh "cf create-service-key $serviceName $keyName"
    echo "Service key '${keyName}' created in service instance '${serviceName}'"
}

def deployToProduction(String appName) {
    def params = [
            appName        : appName,
            //hostname                  : 'bankieren',
            //storePasswordCredentialsId: 'store_password',
            services       : [
                    "app-autoscaler": [
                            registryServiceName: "app-autoscaler",
                            plan               : "standard",
                            serviceName        : "app-autoscaler"
                    ]
            ],
            gitRepo        : 'https://git.rabobank.nl/projects/REL/repos/',
            gitProjectName : 'relevance-profile-capture',
            cfCredentialsId: CREDENTIALS_ID,
            cfOrg          : 'ris-org',
            cfSpace        : 'nutella-space',
            //cfDomainProdWest          : '',
            //cfDomainProdNorth         : ''
    ]

    blueGreenDeployToProduction(params)
}