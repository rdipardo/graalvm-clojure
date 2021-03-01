# monger

Testing whether [monger](https://github.com/michaelklishin/monger) can be used with MongoDB in a native binary image with GraalVM.

## Usage

Currently testing:

    [com.novemberain/monger "3.5.0"]
    [org.clojure/clojure "1.10.2"]

    OpenJDK Runtime Environment GraalVM CE 21.0.0.2 (build 11.0.10+8-jvmci-21.0-b06)
    OpenJDK 64-Bit Server VM GraalVM CE 21.0.0.2 (build 11.0.10+8-jvmci-21.0-b06, mixed mode, sharing)

    Leiningen 2.9.5 on Java 11.0.10 OpenJDK 64-Bit Server VM

Reveal class dependencies:

    make deps # reports are written to `./clinit.d`

Test with:

    make

## Results
`[monger.core :as mg]` :x:   
`[monger.collection :as mc]` :x:   
`[monger.credentials :as mcr]` :x:   

## Attempted workaround #1

Following the suggestion of [this thread](https://github.com/oracle/graal/issues/712#issuecomment-466843789), delay initializing `sun.security.ssl.SSLContextImpl$DefaultSSLContextHolder` until runtime:

```clojure
; project.clj

	#"--initialize-at-run-time=sun.security.ssl.SSLContextImpl$DefaultSSLContextHolder,com.mongodb.UnixServerAddress,com.mongodb.internal.connection.SnappyCompressor,com.mongodb.internal.connection.UnixSocketChannelStream"
```

## Notes
Compilation generates a crippled "fallback image" that can't run:
```
[./target/monger:19688]    classlist:  12,464.91 ms,  0.96 GB
[./target/monger:19688]        (cap):   4,522.08 ms,  0.96 GB
[./target/monger:19688]        setup:  15,941.78 ms,  0.96 GB
[./target/monger:19688]     (clinit):   3,208.03 ms,  1.48 GB
[./target/monger:19688]   (typeflow):  96,748.31 ms,  1.48 GB
[./target/monger:19688]    (objects):  63,207.49 ms,  1.48 GB
[./target/monger:19688]   (features):   5,106.57 ms,  1.48 GB
[./target/monger:19688]     analysis: 177,782.57 ms,  1.48 GB
Warning: Aborting stand-alone image build. Unsupported features in 2 methods
Detailed message:
Error: Class initialization of org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter failed. Use the option --initialize-at-run-time=org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter to explicitly request delayed initialization of this class.
Original exception that caused the problem: java.lang.ExceptionInInitializerError
	at org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter.<clinit>(DateTimeFormatter.java:94)
	at java.base/jdk.internal.misc.Unsafe.ensureClassInitialized0(Native Method)
	at java.base/jdk.internal.misc.Unsafe.ensureClassInitialized(Unsafe.java:1042)
	at jdk.unsupported/sun.misc.Unsafe.ensureClassInitialized(Unsafe.java:698)
	at com.oracle.svm.hosted.classinitialization.ConfigurableClassInitialization.ensureClassInitialized(ConfigurableClassInitialization.java:174)
	at com.oracle.svm.hosted.classinitialization.ConfigurableClassInitialization.computeInitKindAndMaybeInitializeClass(ConfigurableClassInitialization.java:607)
	at com.oracle.svm.hosted.classinitialization.ConfigurableClassInitialization.computeInitKindAndMaybeInitializeClass(ConfigurableClassInitialization.java:127)
	at com.oracle.svm.hosted.classinitialization.ConfigurableClassInitialization.shouldInitializeAtRuntime(ConfigurableClassInitialization.java:160)
	at com.oracle.svm.hosted.snippets.ReflectionPlugins.processForName(ReflectionPlugins.java:168)
	at com.oracle.svm.hosted.snippets.ReflectionPlugins.access$000(ReflectionPlugins.java:63)
	at com.oracle.svm.hosted.snippets.ReflectionPlugins$1.apply(ReflectionPlugins.java:98)
	at jdk.internal.vm.compiler/org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin.execute(InvocationPlugin.java:189)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.applyInvocationPlugin(BytecodeParser.java:2204)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.tryInvocationPlugin(BytecodeParser.java:2190)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendInvoke(BytecodeParser.java:1895)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genInvokeStatic(BytecodeParser.java:1654)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genInvokeStatic(BytecodeParser.java:1634)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBytecode(BytecodeParser.java:5406)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.iterateBytecodesForBlock(BytecodeParser.java:3436)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3243)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.build(BytecodeParser.java:1109)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.parseAndInlineCallee(BytecodeParser.java:2608)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.inline(BytecodeParser.java:2506)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.tryInline(BytecodeParser.java:2248)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendInvoke(BytecodeParser.java:1903)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genInvokeStatic(BytecodeParser.java:1654)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genInvokeStatic(BytecodeParser.java:1634)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBytecode(BytecodeParser.java:5406)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.iterateBytecodesForBlock(BytecodeParser.java:3436)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3243)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.build(BytecodeParser.java:1109)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.buildRootMethod(BytecodeParser.java:1003)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.GraphBuilderPhase$Instance.run(GraphBuilderPhase.java:84)
	at com.oracle.svm.hosted.phases.SharedGraphBuilderPhase.run(SharedGraphBuilderPhase.java:76)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.run(Phase.java:49)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.BasePhase.apply(BasePhase.java:212)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.BasePhase.apply(BasePhase.java:147)
	at com.oracle.svm.hosted.substitute.UnsafeAutomaticSubstitutionProcessor.getStaticInitializerGraph(UnsafeAutomaticSubstitutionProcessor.java:1045)
	at com.oracle.svm.hosted.substitute.UnsafeAutomaticSubstitutionProcessor.computeSubstitutions(UnsafeAutomaticSubstitutionProcessor.java:352)
	at com.oracle.svm.hosted.SVMHost.initializeType(SVMHost.java:286)
	at com.oracle.graal.pointsto.meta.AnalysisType.lambda$new$0(AnalysisType.java:227)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at com.oracle.graal.pointsto.util.AnalysisFuture.ensureDone(AnalysisFuture.java:64)
	at com.oracle.graal.pointsto.meta.AnalysisType.ensureInitialized(AnalysisType.java:600)
	at com.oracle.graal.pointsto.meta.AnalysisUniverse.lookupAllowUnresolved(AnalysisUniverse.java:364)
	at com.oracle.graal.pointsto.infrastructure.AnalysisConstantPool.lookupField(AnalysisConstantPool.java:51)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.lookupField(BytecodeParser.java:4360)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.genGetStatic(BytecodeParser.java:4914)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBytecode(BytecodeParser.java:5400)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.iterateBytecodesForBlock(BytecodeParser.java:3436)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3243)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.build(BytecodeParser.java:1109)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.buildRootMethod(BytecodeParser.java:1003)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.GraphBuilderPhase$Instance.run(GraphBuilderPhase.java:84)
	at com.oracle.svm.hosted.phases.SharedGraphBuilderPhase.run(SharedGraphBuilderPhase.java:76)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.run(Phase.java:49)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.BasePhase.apply(BasePhase.java:212)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.apply(Phase.java:42)
	at jdk.internal.vm.compiler/org.graalvm.compiler.phases.Phase.apply(Phase.java:38)
	at com.oracle.graal.pointsto.flow.MethodTypeFlowBuilder.parse(MethodTypeFlowBuilder.java:223)
	at com.oracle.graal.pointsto.flow.MethodTypeFlowBuilder.apply(MethodTypeFlowBuilder.java:357)
	at com.oracle.graal.pointsto.flow.MethodTypeFlow.doParse(MethodTypeFlow.java:313)
	at com.oracle.graal.pointsto.flow.MethodTypeFlow.ensureParsed(MethodTypeFlow.java:302)
	at com.oracle.graal.pointsto.flow.MethodTypeFlow.addContext(MethodTypeFlow.java:103)
	at com.oracle.graal.pointsto.flow.StaticInvokeTypeFlow.update(InvokeTypeFlow.java:434)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Caused by: java.lang.ClassNotFoundException: javax.xml.bind.DatatypeConverter
	at java.base/java.net.URLClassLoader.findClass(URLClassLoader.java:471)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:589)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:315)
	at org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter.<clinit>(DateTimeFormatter.java:87)
	... 73 more
Error: No instances of sun.security.provider.NativePRNG are allowed in the image heap as this class should be initialized at image runtime. To see how this object got instantiated use --trace-object-instantiation=sun.security.provider.NativePRNG.
Trace: Object was reached by
	reading field java.security.SecureRandom.secureRandomSpi of
		constant java.security.SecureRandom@831efe7 reached by
	reading field sun.security.ssl.SSLContextImpl.secureRandom of
		constant sun.security.ssl.SSLContextImpl$DefaultSSLContext@9d2103c reached by
	reading field sun.security.ssl.SSLSocketFactoryImpl.context of
		constant sun.security.ssl.SSLSocketFactoryImpl@6f56b32a reached by
	scanning method com.mongodb.MongoClientOptions.getSocketFactory(MongoClientOptions.java:703)
Call path from entry point to com.mongodb.MongoClientOptions.getSocketFactory():
	at com.mongodb.MongoClientOptions.getSocketFactory(MongoClientOptions.java:700)
	at com.mongodb.Mongo.createCluster(Mongo.java:757)
	at com.mongodb.Mongo.createCluster(Mongo.java:743)
	at com.mongodb.Mongo.<init>(Mongo.java:295)
	at com.mongodb.Mongo.<init>(Mongo.java:290)
	at com.mongodb.MongoClient.<init>(MongoClient.java:195)
	at monger.core$connect.invokeStatic(core.clj:91)
	at monger.core$connect.invoke(core.clj:66)
	at clojure.lang.AFn.applyToHelper(AFn.java:160)
	at clojure.lang.RestFn.applyTo(RestFn.java:132)
	at simple.main.main(Unknown Source)
	at com.oracle.svm.core.JavaMainWrapper.runCore(JavaMainWrapper.java:146)
	at com.oracle.svm.core.JavaMainWrapper.run(JavaMainWrapper.java:182)
	at com.oracle.svm.core.code.IsolateEnterStub.JavaMainWrapper_run_5087f5482cc9a6abc971913ece43acb471d2631b(generated:0)

Warning: Use -H:+ReportExceptionStackTraces to print stacktrace of underlying exception
[./target/monger:19778]    classlist:   8,120.61 ms,  0.96 GB
[./target/monger:19778]        (cap):   4,631.08 ms,  0.96 GB
[./target/monger:19778]        setup:  14,006.28 ms,  0.96 GB
[./target/monger:19778]     (clinit):   1,376.96 ms,  1.20 GB
[./target/monger:19778]   (typeflow):  28,704.50 ms,  1.20 GB
[./target/monger:19778]    (objects):  17,651.74 ms,  1.20 GB
[./target/monger:19778]   (features):   1,046.31 ms,  1.20 GB
[./target/monger:19778]     analysis:  50,056.80 ms,  1.20 GB
[./target/monger:19778]     universe:   2,333.66 ms,  1.20 GB
[./target/monger:19778]      (parse):  11,862.23 ms,  1.47 GB
[./target/monger:19778]     (inline):   6,946.07 ms,  1.47 GB
[./target/monger:19778]    (compile):  56,770.63 ms,  1.44 GB
[./target/monger:19778]      compile:  78,415.12 ms,  1.44 GB
[./target/monger:19778]        image:   6,630.71 ms,  1.44 GB
[./target/monger:19778]        write:     953.34 ms,  1.44 GB
[./target/monger:19778]      [total]: 161,649.42 ms,  1.44 GB
Warning: Image './target/monger' is a fallback image that requires a JDK for execution (use --no-fallback to suppress fallback image generation and to print more detailed information why a fallback image was necessary).
Building with native build. Learn about native build in Compose here: https://docs.docker.com/go/compose-native-build/
Recreating monger_database_1 ... done
Waiting for database . . .
./target/monger
Error: Could not find or load main class simple.main
Caused by: java.lang.ClassNotFoundException: simple.main
```
