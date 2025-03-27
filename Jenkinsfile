pipeline { 
   agent any 
   environment {
      GIT_REPO = 'YYY'
      GIT_CREDENTIAL_ID = 'c0e8826e-5e2c-4c1c-a484-6c17d53ac539'
      SONARQUBE_URL = 'http://10.20.84.26:8082/sonar'
      SONAR_TOKEN = credentials('sonar-login')
   }
   stages { 
      stage('Checkout') { 
         steps {
            scmSkip(deleteBuild: true, skipPattern:'.*\\[ci-skip\\].*')

            git branch: 'main', 
               credentialsId: env.GIT_CREDENTIAL_ID,
               url: 'https://github.com/UDFJDC-ModelosProgramacion/' + env.GIT_REPO
         }
      }
      stage('GitInspector') { 
         steps {
            withCredentials([usernamePassword(credentialsId: env.GIT_CREDENTIAL_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
               sh 'mkdir -p code-analyzer-report'
               sh """ curl --request POST --url https://code-analyzer.virtual.udistrital.edu.co/analyze --header "Content-Type: application/json" --data '{"repo_url":"git@github.com:UDFJDC-ModelosProgramacion/${GIT_REPO}.git", "access_token": "${GIT_PASSWORD}" }' > code-analyzer-report/index.html """   
            }
            publishHTML (target: [
               allowMissing: false,
               alwaysLinkToLastBuild: false,
               keepAll: true,
               reportDir: 'code-analyzer-report',
               reportFiles: 'index.html',
               reportName: "GitInspector"
            ])
         }
      }
      stage('Build') {
         // Build artifacts
         steps {
            script {
               docker.image('citools-isis2603:latest').inside('-v $HOME/.m2:/root/.m2:z -u root') {
                  sh '''
                     java -version
                     mvn clean install
                  '''
               }
            }
         }
      }
      stage('Testing') {
         // Run unit tests
         steps {
            script {
               docker.image('citools-isis2603:latest').inside('-v $HOME/.m2:/root/.m2:z -u root') {                  
                  sh '''
                     mvn test
                  '''
               }
            }
         }
      }
      stage('Static Analysis') {
         // Run static analysis
         steps {
            script {
               docker.image('citools-isis2603:latest').inside('-v $HOME/.m2:/root/.m2:z -u root') {
                  sh '''
                     mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN} -Dsonar.host.url=${SONARQUBE_URL}
                  '''
               }
            }
         }
      }
      stage('ARCC') {
         // Run arcc analysis
         steps {
            script {
               docker.image('arcc-tools-isis2603:latest').inside('-e ARCHID_TOKEN=${ARCHID_TOKEN}'){
                  sh '''
                     java -version
                     rsync --recursive . bookstore-back
                     java -cp /eclipse/plugins/org.eclipse.equinox.launcher_1.5.700.v20200207-2156.jar org.eclipse.equinox.launcher.Main -application co.edu.udistrital.archtoring.archtoring bookstore-back
                  '''
               }
            }
         }
      }      
   }
   post {
      always {
        cleanWs()
        deleteDir() 
        dir("${env.GIT_REPO}@tmp") {
          deleteDir()
        }
      }
   }
}
