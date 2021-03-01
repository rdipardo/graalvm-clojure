(defproject monger "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.10.2"]
                 [com.novemberain/monger "3.5.0"]
                 ]

  :main simple.main

  :uberjar-name "simple-main.jar"

  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-shell "0.5.0"]]}}

  :aliases
  {"native"
   ["shell"
    "native-image" 
    "--report-unsupported-elements-at-runtime" 
    "--trace-object-instantiation=sun.security.provider.NativePRNG"
    "--no-server"
    "--initialize-at-build-time"
    "--enable-url-protocols=http,https"
    #"--initialize-at-run-time=org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter,sun.security.ssl.SSLContextImpl$DefaultSSLContextHolder,com.mongodb.UnixServerAddress,com.mongodb.internal.connection.SnappyCompressor,com.mongodb.internal.connection.UnixSocketChannelStream"
    "-jar" "./target/${:uberjar-name:-${:name}-${:version}-standalone.jar}"
    "-H:+AllowIncompleteClasspath"
    "-H:ConfigurationFileDirectories=clinit.d"
    "-H:Name=./target/${:name}"]

   "run-native" ["shell" "./target/${:name}"]})
