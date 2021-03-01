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

## Attempted workaround #3

Adding this to the configuration for extra diagnostics:

```clojure
; project.clj

	"--trace-object-instantiation=sun.security.provider.NativePRNG"
```

generates the same crippled binary as before, but now with a helpful suggestion:

> To fix the issue mark sun.security.provider.NativePRNG for build-time
> initialization with --initialize-at-build-time=sun.security.provider.NativePRNG
> or use the the information from the trace to find the culprit and
> --initialize-at-run-time=<culprit> to prevent its instantiation.

Taking the first option swiftly crashes the build:
```
[./target/monger:6952]    classlist:  12,683.39 ms,  0.96 GB
[./target/monger:6952]        (cap):   2,560.21 ms,  0.96 GB
[./target/monger:6952]        setup:   7,383.27 ms,  0.96 GB
Error: Incompatible change of initialization policy for sun.security.provider.NativePRNG: trying to change BUILD_TIME from the command line to RERUN for substitutions
Error: Use -H:+ReportExceptionStackTraces to print stacktrace of underlying exception
Error: Image build request failed with exit status 1
```

(I can confirm that passing the same class to `--initialize-at-run-time` gets the
identical result except with `RUN_TIME` in place of `BUILD_TIME`.)

The second option would be RT initialization of the "culprit", to be chosen from
the call chain provided in the complete stack trace:
```
Warning: Aborting stand-alone image build. No instances of sun.security.provider.NativePRNG are allowed in the image heap as this class should be initialized at image runtime. Object has been initialized by the simple.main class initializer with a trace:
 	at sun.security.provider.NativePRNG.<init>(NativePRNG.java:205)
	at jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Unknown Source)
	at jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:490)
	at java.security.Provider.newInstanceUtil(Provider.java:176)
	at java.security.Provider$Service.newInstance(Provider.java:1894)
	at java.security.SecureRandom.getDefaultPRNG(SecureRandom.java:290)
	at java.security.SecureRandom.<init>(SecureRandom.java:219)
	at sun.security.ssl.JsseJce.getSecureRandom(JsseJce.java:281)
	at sun.security.ssl.SSLContextImpl.engineInit(SSLContextImpl.java:97)
	at sun.security.ssl.SSLContextImpl$DefaultSSLContext.<init>(SSLContextImpl.java:1203)
	at jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Unknown Source)
	at jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:490)
	at java.security.Provider.newInstanceUtil(Provider.java:154)
	at java.security.Provider$Service.newInstance(Provider.java:1894)
	at sun.security.jca.GetInstance.getInstance(GetInstance.java:236)
	at sun.security.jca.GetInstance.getInstance(GetInstance.java:164)
	at javax.net.ssl.SSLContext.getInstance(SSLContext.java:168)
	at javax.net.ssl.SSLContext.getDefault(SSLContext.java:99)
	at javax.net.ssl.SSLSocketFactory.getDefault(SSLSocketFactory.java:123)
	at com.mongodb.MongoClientOptions.<clinit>(MongoClientOptions.java:59)
	at java.lang.Class.forName0(Unknown Source)
	at java.lang.Class.forName(Class.java:398)
	at clojure.lang.RT.classForName(RT.java:2212)
	at clojure.lang.RT.classForName(RT.java:2221)
	at monger.core__init.__init0(Unknown Source)
	at monger.core__init.<clinit>(Unknown Source)
	at java.lang.Class.forName0(Unknown Source)
	at java.lang.Class.forName(Class.java:398)
	at clojure.lang.RT.classForName(RT.java:2212)
	at clojure.lang.RT.classForName(RT.java:2221)
	at clojure.lang.RT.loadClassForName(RT.java:2240)
	at clojure.lang.RT.load(RT.java:449)
	at clojure.lang.RT.load(RT.java:424)
	at clojure.core$load$fn__6857.invoke(core.clj:6115)
	at clojure.core$load.invokeStatic(core.clj:6114)
	at clojure.core$load.doInvoke(core.clj:6098)
	at clojure.lang.RestFn.invoke(RestFn.java:408)
	at clojure.core$load_one.invokeStatic(core.clj:5897)
	at clojure.core$load_one.invoke(core.clj:5892)
	at clojure.core$load_lib$fn__6797.invoke(core.clj:5937)
	at clojure.core$load_lib.invokeStatic(core.clj:5936)
	at clojure.core$load_lib.doInvoke(core.clj:5917)
	at clojure.lang.RestFn.applyTo(RestFn.java:142)
	at clojure.core$apply.invokeStatic(core.clj:669)
	at clojure.core$load_libs.invokeStatic(core.clj:5974)
	at clojure.core$load_libs.doInvoke(core.clj:5958)
	at clojure.lang.RestFn.applyTo(RestFn.java:137)
	at clojure.core$apply.invokeStatic(core.clj:669)
	at clojure.core$require.invokeStatic(core.clj:5996)
	at clojure.core$require.doInvoke(core.clj:5996)
	at clojure.lang.RestFn.invoke(RestFn.java:457)
	at simple.main$loading__6738__auto____171.invoke(main.clj:1)
	at simple.main__init.load(Unknown Source)
	at simple.main__init.<clinit>(Unknown Source)
	at java.lang.Class.forName0(Unknown Source)
	at java.lang.Class.forName(Class.java:398)
	at clojure.lang.RT.classForName(RT.java:2212)
	at clojure.lang.RT.classForName(RT.java:2221)
	at clojure.lang.RT.loadClassForName(RT.java:2240)
	at clojure.lang.RT.load(RT.java:449)
	at clojure.lang.RT.load(RT.java:424)
	at clojure.core$load$fn__6857.invoke(core.clj:6115)
	at clojure.core$load.invokeStatic(core.clj:6114)
	at clojure.core$load.doInvoke(core.clj:6098)
	at clojure.lang.RestFn.invoke(RestFn.java:408)
	at clojure.lang.Var.invoke(Var.java:384)
	at clojure.lang.Util.loadWithClass(Util.java:251)
	at simple.main.<clinit>(Unknown Source)
.  To fix the issue mark sun.security.provider.NativePRNG for build-time initialization with --initialize-at-build-time=sun.security.provider.NativePRNG or use the the information from the trace to find the culprit and --initialize-at-run-time=<culprit> to prevent its instantiation.

Detailed message:
Trace: Object was reached by
	reading field java.security.SecureRandom.secureRandomSpi of
		constant java.security.SecureRandom@6438ef0c reached by
	reading field sun.security.ssl.SSLContextImpl.secureRandom of
		constant sun.security.ssl.SSLContextImpl$DefaultSSLContext@2bb02e9f reached by
	reading field sun.security.ssl.SSLSocketFactoryImpl.context of
		constant sun.security.ssl.SSLSocketFactoryImpl@672fe6cf reached by
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
	at clojure.spec.alpha$multi_spec_impl$reify__2075$gen__2077$fn__2081.invoke(alpha.clj:988)
	at clojure.lang.AFn.run(AFn.java:22)
	at java.lang.Thread.run(Thread.java:834)
	at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:519)
	at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)
	at com.oracle.svm.core.code.IsolateEnterStub.PosixJavaThreads_pthreadStartRoutine_e1f4a8c0039f8337338252cd8734f63a79b5e3df(generated:0)

Warning: Use -H:+ReportExceptionStackTraces to print stacktrace of underlying exception
[./target/monger:6053]    classlist:   9,214.85 ms,  0.96 GB
[./target/monger:6053]        (cap):   5,353.00 ms,  0.96 GB
[./target/monger:6053]        setup:  15,822.46 ms,  0.96 GB
[./target/monger:6053]     (clinit):   1,281.69 ms,  1.47 GB
[./target/monger:6053]   (typeflow):  29,801.14 ms,  1.47 GB
[./target/monger:6053]    (objects):  18,415.93 ms,  1.47 GB
[./target/monger:6053]   (features):   1,042.13 ms,  1.47 GB
[./target/monger:6053]     analysis:  51,564.12 ms,  1.47 GB
[./target/monger:6053]     universe:   2,353.70 ms,  1.47 GB
[./target/monger:6053]      (parse):  12,583.34 ms,  1.47 GB
[./target/monger:6053]     (inline):   7,214.90 ms,  1.47 GB
[./target/monger:6053]    (compile):  57,896.22 ms,  1.40 GB
[./target/monger:6053]      compile:  80,581.89 ms,  1.40 GB
[./target/monger:6053]        image:   6,940.22 ms,  1.27 GB
[./target/monger:6053]        write:     905.26 ms,  1.27 GB
[./target/monger:6053]      [total]: 168,590.00 ms,  1.27 GB
Warning: Image './target/monger' is a fallback image that requires a JDK for execution (use --no-fallback to suppress fallback image generation and to print more detailed information why a fallback image was necessary).
Building with native build. Learn about native build in Compose here: https://docs.docker.com/go/compose-native-build/
Recreating monger_database_1 ... done
Waiting for database . . .
./target/monger
Error: Could not find or load main class simple.main
Caused by: java.lang.ClassNotFoundException: simple.main
```

## Attempted workaround #2

Last time the error message began with:

```
Warning: Aborting stand-alone image build. Unsupported features in 2 methods
Detailed message:
Error: Class initialization of org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter failed. Use the option --initialize-at-run-time=org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter to explicitly request delayed initialization of this class.
. . . .
```

Why not?

```clojure
; project.clj

	#"--initialize-at-run-time=org.bson.json.DateTimeFormatter$JaxbDateTimeFormatter,sun.security.ssl.SSLContextImpl$DefaultSSLContextHolder,com.mongodb.UnixServerAddress,com.mongodb.internal.connection.SnappyCompressor,com.mongodb.internal.connection.UnixSocketChannelStream"
```

## Notes
Another crippled "fallback image" is generated, but with a much cleaner stack trace:
```
[./target/monger:23844]    classlist:  11,060.42 ms,  0.96 GB
[./target/monger:23844]        (cap):   4,441.75 ms,  0.96 GB
[./target/monger:23844]        setup:  15,418.85 ms,  0.96 GB
[./target/monger:23844]     (clinit):   2,887.05 ms,  1.45 GB
[./target/monger:23844]   (typeflow):  99,129.04 ms,  1.45 GB
[./target/monger:23844]    (objects):  65,146.99 ms,  1.45 GB
[./target/monger:23844]   (features):   4,762.56 ms,  1.45 GB
[./target/monger:23844]     analysis: 181,215.57 ms,  1.45 GB
Warning: Aborting stand-alone image build. No instances of sun.security.provider.NativePRNG are allowed in the image heap as this class should be initialized at image runtime. To see how this object got instantiated use --trace-object-instantiation=sun.security.provider.NativePRNG.
Detailed message:
Trace: Object was reached by
	reading field java.security.SecureRandom.secureRandomSpi of
		constant java.security.SecureRandom@698a15f8 reached by
	reading field sun.security.ssl.SSLContextImpl.secureRandom of
		constant sun.security.ssl.SSLContextImpl$DefaultSSLContext@401e547f reached by
	reading field sun.security.ssl.SSLSocketFactoryImpl.context of
		constant sun.security.ssl.SSLSocketFactoryImpl@15a718be reached by
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
	at clojure.lang.Keyword.applyTo(Keyword.java:253)
	at simple.main.main(Unknown Source)
	at com.oracle.svm.core.JavaMainWrapper.runCore(JavaMainWrapper.java:146)
	at com.oracle.svm.core.JavaMainWrapper.run(JavaMainWrapper.java:182)
	at com.oracle.svm.core.code.IsolateEnterStub.JavaMainWrapper_run_5087f5482cc9a6abc971913ece43acb471d2631b(generated:0)

Warning: Use -H:+ReportExceptionStackTraces to print stacktrace of underlying exception
[./target/monger:24201]    classlist:   7,536.55 ms,  0.96 GB
[./target/monger:24201]        (cap):   3,428.05 ms,  0.96 GB
[./target/monger:24201]        setup:  12,871.21 ms,  0.96 GB
[./target/monger:24201]     (clinit):   1,064.89 ms,  1.47 GB
[./target/monger:24201]   (typeflow):  29,950.13 ms,  1.47 GB
[./target/monger:24201]    (objects):  17,838.72 ms,  1.47 GB
[./target/monger:24201]   (features):   1,298.75 ms,  1.47 GB
[./target/monger:24201]     analysis:  51,223.41 ms,  1.47 GB
[./target/monger:24201]     universe:   2,476.58 ms,  1.47 GB
[./target/monger:24201]      (parse):  12,670.52 ms,  1.47 GB
[./target/monger:24201]     (inline):   6,935.83 ms,  1.47 GB
[./target/monger:24201]    (compile):  58,093.97 ms,  1.40 GB
[./target/monger:24201]      compile:  80,550.21 ms,  1.40 GB
[./target/monger:24201]        image:   6,771.29 ms,  1.27 GB
[./target/monger:24201]        write:     905.91 ms,  1.27 GB
[./target/monger:24201]      [total]: 163,383.31 ms,  1.27 GB
Warning: Image './target/monger' is a fallback image that requires a JDK for execution (use --no-fallback to suppress fallback image generation and to print more detailed information why a fallback image was necessary).
Building with native build. Learn about native build in Compose here: https://docs.docker.com/go/compose-native-build/
Recreating monger_database_1 ... done
Waiting for database . . .
./target/monger
Error: Could not find or load main class simple.main
Caused by: java.lang.ClassNotFoundException: simple.main
```

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
