def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {
	    // Clean workspace before doing anything
	    deleteDir()

	    try {
	        stage ('Clone') {
	        	checkout scm
	        }
	        stage ('Build') {
	        	sh "echo 'building ${config.yourteam} ...'"
                sh "docker image build -t ${config.yourteam}/${config.imagename} ."
                sh "docker tag ${config.yourteam}/${imagename} ${config.yourteam}/${config.imagename}:${config.ImageTag}"
                sh "docker tag ${config.yourteam}/${config.imagename} ${config.yourteam}/${config.imagename}:latest"
                withCredentials([usernamePassword(
                      credentialsId: "docker",
                      usernameVariable: "USER",
                      passwordVariable: "PASS"
                )]) {
                    sh "docker login -u '$USER' -p '$PASS'"
               }
               sh "docker image push ${config.yourteam}/${config.imagename}:${config.ImageTag}"
               sh "docker image push ${config.yourteam}/${config.imagename}:latest"
            }    
        }
    }
}
