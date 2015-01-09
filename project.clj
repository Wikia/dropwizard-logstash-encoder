(defproject com.wikia/dropwizard-logstash-encoder "1.0.0"
  :description "Addon for dropwizard to log using the logback-logstash-encoder (see https://github.com/logstash/logstash-logback-encoder)"
  :url "https://github.com/Wikia/dropwizard-logback-logsash-encoder"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:url "git@github.com:Wikia/dropwizard-logstash-encoder.git"}
  :pom-addition [:developers [:developer
                              [:name "Nelson Monterroso"]
                              [:url "http://github.com/nmonterroso"]
                              [:email "nelson@wikia-inc.com"]]]
  :pom-plugins [[org.apache.maven.plugins/maven-source-plugin "2.2.1"
                 {:executions [:execution
                               [:id "attach-sources"]
                               [:goals ([:goal "jar-no-fork"])]]}]
                [org.apache.maven.plugins/maven-javadoc-plugin "2.9.1"
                 {:executions [:execution
                               [:id "attach-javadocs"]
                               [:goals ([:goal "jar"])]]}]]
  :deploy-repositories {"releases" {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2" :creds :gpg}
                        "snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots" :creds :gpg}}
  :dependencies [[io.dropwizard/dropwizard-logging "0.7.0"]
                 [net.logstash.logback/logstash-logback-encoder "3.5"]])
