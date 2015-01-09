(defproject com.wikia/dropwizard-logstash-encoder "0.1.0-SNAPSHOT"
  :description "Addon for dropwizard to log using the logback-logstash-encoder (see https://github.com/logstash/logstash-logback-encoder)"
  :url "https://github.com/Wikia/dropwizard-logback-logsash-encoder"
  :scm {:url "git@github.com:Wikia/dropwizard-logback-logstash-encoder.git"}
  :pom-addition [:developers [:developer
                              [:name "Nelson Monterroso"]
                              [:url "http://github.com/nmonterroso"]
                              [:email "nelson@wikia-inc.com"]]]
  :deploy-repositories {"releases" {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2/" :creds :gpg}
                        "snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots/" :creds :gpg}}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[io.dropwizard/dropwizard-logging "0.7.0"]
                 [net.logstash.logback/logstash-logback-encoder "3.5"]])
