pipeline {
    agent none
    stages {
        stage('Build') {
		    agent {
		        docker {
		            image 'maven:3-alpine'
		            args '-v /root/.m2:/root/.m2'
			    }
		    }         
            steps {
                sh 'mvn -B -DskipTests clean package'
                archiveArtifacts 'target/*.jar'
                stash includes: 'target/*.jar', name: 'jar'
            }
        }
        stage('Build Images') { 
        	agent any
            steps {
            		unstash 'jar'
            		sh 'docker build -f Dockerfile.pipe -t misegr/iot-kafka-pipe .'
            		sh 'docker build -f Dockerfile.consumer -t misegr/iot-kafka-consumer .'
            		sh 'docker build -f Dockerfile.alert -t misegr/iot-kafka-alert .'
            }
        }
    }
}
