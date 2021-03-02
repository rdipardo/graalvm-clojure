# lacinia

Testing whether the [lacinia](https://github.com/walmartlabs/lacinia)
library can be used in a native binary image with GraalVM.

## Usage

Currently testing:

    [org.clojure/clojure "1.10.1"]
    [com.walmartlabs/lacinia "0.39-alpha-5"]

Configure with:

    make deps

Rerun this step after changing any project dependencies.

The main class loads a schema file from disk, so `native-image` must be run with
the `-H:ResourceConfigurationFiles` set to `clinit.d/resource-config.json` (as
`project.clj` should be doing already).

More information [here](https://www.graalvm.org/reference-manual/native-image/Resources).

Test with:

    make

## Results

_simple.main_
- `[com.walmartlabs.lacinia :as lacinia]` :x:

_simple.schema_
- `[com.walmartlabs.lacinia.util :as util]` :x:
- `[com.walmartlabs.lacinia.schema :as schema]` :x:

## Notes

Possible instance of [this clojure bug](https://github.com/oracle/graal/issues/1681).
Further details [here](https://clojure.atlassian.net/browse/CLJ-1472).

```
[./target/lacinia:27686]    classlist:  13,442.01 ms,  0.96 GB
[./target/lacinia:27686]        (cap):   4,460.52 ms,  0.96 GB
[./target/lacinia:27686]        setup:  14,981.79 ms,  0.96 GB
[./target/lacinia:27686]     (clinit):   4,274.06 ms,  1.50 GB
[./target/lacinia:27686]   (typeflow): 122,821.70 ms,  1.50 GB
[./target/lacinia:27686]    (objects): 104,743.52 ms,  1.50 GB
[./target/lacinia:27686]   (features):   7,438.27 ms,  1.50 GB
[./target/lacinia:27686]     analysis: 251,795.08 ms,  1.50 GB
Error: Unsupported features in 4 methods
Detailed message:
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,3,4,6,_,_] stack: [9] locks: [] rethrowException]
Other frame state: [locals: [1,2,3,4,6,_,_] stack: [23] locks: [16 / 7] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:183) [bci: 17, intrinsic: false]
  17: checkcast     #31         // clojure.lang.IFn
  20: aload_0
  21: getfield      #2          // orig:java.lang.Object
  24: aconst_null
  25: getstatic     #5          // java.lang.System.out:java.io.PrintStream

Call path from entry point to clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(Object, Object, Object):
	at clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:166)
	at com.walmartlabs.lacinia.util$inject_enum_transformers$fn__500.invoke(util.clj:123)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,3,4,6,_,_] stack: [9] locks: [] rethrowException]
Other frame state: [locals: [1,2,3,4,6,_,_] stack: [23] locks: [16 / 7] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:183) [bci: 17, intrinsic: false]
  17: checkcast     #31         // clojure.lang.IFn
  20: aload_0
  21: getfield      #2          // orig:java.lang.Object
  24: aconst_null
  25: getstatic     #5          // java.lang.System.out:java.io.PrintStream

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,41,_,_,_,_] stack: [44] locks: [] rethrowException]
Other frame state: [locals: [1,2,41,_,_,_,_] stack: [74] locks: [51 / 42] rethrowException]
Parser context: clojure.core.server$io_prepl$fn__8955.invoke(server.clj:287) [bci: 84, intrinsic: false]
  84: dup_x2
  85: if_acmpeq     92
  88: pop
  89: goto          114
  92: swap
  93: pop
  94: dup

Call path from entry point to clojure.core.server$io_prepl$fn__8955.invoke(Object):
	at clojure.core.server$io_prepl$fn__8955.invoke(server.clj:284)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,41,_,_,_,_] stack: [44] locks: [] rethrowException]
Other frame state: [locals: [1,2,41,_,_,_,_] stack: [74] locks: [51 / 42] rethrowException]
Parser context: clojure.core.server$io_prepl$fn__8955.invoke(server.clj:287) [bci: 84, intrinsic: false]
  84: dup_x2
  85: if_acmpeq     92
  88: pop
  89: goto          114
  92: swap
  93: pop
  94: dup

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:190) [bci: 15, intrinsic: false]
  15: checkcast     #30         // clojure.lang.IFn
  18: aload_0
  19: getfield      #2          // orig:java.lang.Object
  22: invokeinterface#5, 2       // clojure.lang.IFn.invoke:(java.lang.Object)java.lang.Object

Call path from entry point to clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke():
	at clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:186)
	at clojure.core$pcalls$fn__8483.invoke(core.clj:7042)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:190) [bci: 15, intrinsic: false]
  15: checkcast     #30         // clojure.lang.IFn
  18: aload_0
  19: getfield      #2          // orig:java.lang.Object
  22: invokeinterface#5, 2       // clojure.lang.IFn.invoke:(java.lang.Object)java.lang.Object

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:22) [bci: 13, intrinsic: false]
  13: checkcast     #30         // clojure.lang.IFn
  16: getstatic     #5          // const__1:clojure.lang.Var
  19: invokevirtual #4          // clojure.lang.Var.getRawRoot:()java.lang.Object
  22: checkcast     #30         // clojure.lang.IFn

Call path from entry point to clojure.spec.gen.alpha$dynaload$fn__2628.invoke():
	at clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:21)
	at clojure.core$pcalls$fn__8483.invoke(core.clj:7042)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:22) [bci: 13, intrinsic: false]
  13: checkcast     #30         // clojure.lang.IFn
  16: getstatic     #5          // const__1:clojure.lang.Var
  19: invokevirtual #4          // clojure.lang.Var.getRawRoot:()java.lang.Object
  22: checkcast     #30         // clojure.lang.IFn

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)

com.oracle.svm.core.util.UserError$UserException: Unsupported features in 4 methods
Detailed message:
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,3,4,6,_,_] stack: [9] locks: [] rethrowException]
Other frame state: [locals: [1,2,3,4,6,_,_] stack: [23] locks: [16 / 7] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:183) [bci: 17, intrinsic: false]
  17: checkcast     #31         // clojure.lang.IFn
  20: aload_0
  21: getfield      #2          // orig:java.lang.Object
  24: aconst_null
  25: getstatic     #5          // java.lang.System.out:java.io.PrintStream

Call path from entry point to clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(Object, Object, Object):
	at clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:166)
	at com.walmartlabs.lacinia.util$inject_enum_transformers$fn__500.invoke(util.clj:123)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,3,4,6,_,_] stack: [9] locks: [] rethrowException]
Other frame state: [locals: [1,2,3,4,6,_,_] stack: [23] locks: [16 / 7] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:183) [bci: 17, intrinsic: false]
  17: checkcast     #31         // clojure.lang.IFn
  20: aload_0
  21: getfield      #2          // orig:java.lang.Object
  24: aconst_null
  25: getstatic     #5          // java.lang.System.out:java.io.PrintStream

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,41,_,_,_,_] stack: [44] locks: [] rethrowException]
Other frame state: [locals: [1,2,41,_,_,_,_] stack: [74] locks: [51 / 42] rethrowException]
Parser context: clojure.core.server$io_prepl$fn__8955.invoke(server.clj:287) [bci: 84, intrinsic: false]
  84: dup_x2
  85: if_acmpeq     92
  88: pop
  89: goto          114
  92: swap
  93: pop
  94: dup

Call path from entry point to clojure.core.server$io_prepl$fn__8955.invoke(Object):
	at clojure.core.server$io_prepl$fn__8955.invoke(server.clj:284)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,41,_,_,_,_] stack: [44] locks: [] rethrowException]
Other frame state: [locals: [1,2,41,_,_,_,_] stack: [74] locks: [51 / 42] rethrowException]
Parser context: clojure.core.server$io_prepl$fn__8955.invoke(server.clj:287) [bci: 84, intrinsic: false]
  84: dup_x2
  85: if_acmpeq     92
  88: pop
  89: goto          114
  92: swap
  93: pop
  94: dup

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:190) [bci: 15, intrinsic: false]
  15: checkcast     #30         // clojure.lang.IFn
  18: aload_0
  19: getfield      #2          // orig:java.lang.Object
  22: invokeinterface#5, 2       // clojure.lang.IFn.invoke:(java.lang.Object)java.lang.Object

Call path from entry point to clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke():
	at clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:186)
	at clojure.core$pcalls$fn__8483.invoke(core.clj:7042)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:190) [bci: 15, intrinsic: false]
  15: checkcast     #30         // clojure.lang.IFn
  18: aload_0
  19: getfield      #2          // orig:java.lang.Object
  22: invokeinterface#5, 2       // clojure.lang.IFn.invoke:(java.lang.Object)java.lang.Object

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:22) [bci: 13, intrinsic: false]
  13: checkcast     #30         // clojure.lang.IFn
  16: getstatic     #5          // const__1:clojure.lang.Var
  19: invokevirtual #4          // clojure.lang.Var.getRawRoot:()java.lang.Object
  22: checkcast     #30         // clojure.lang.IFn

Call path from entry point to clojure.spec.gen.alpha$dynaload$fn__2628.invoke():
	at clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:21)
	at clojure.core$pcalls$fn__8483.invoke(core.clj:7042)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:22) [bci: 13, intrinsic: false]
  13: checkcast     #30         // clojure.lang.IFn
  16: getstatic     #5          // const__1:clojure.lang.Var
  19: invokevirtual #4          // clojure.lang.Var.getRawRoot:()java.lang.Object
  22: checkcast     #30         // clojure.lang.IFn

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)

	at com.oracle.svm.core.util.UserError.abort(UserError.java:82)
	at com.oracle.svm.hosted.FallbackFeature.reportAsFallback(FallbackFeature.java:233)
	at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:773)
	at com.oracle.svm.hosted.NativeImageGenerator.doRun(NativeImageGenerator.java:563)
	at com.oracle.svm.hosted.NativeImageGenerator.lambda$run$0(NativeImageGenerator.java:476)
	at java.base/java.util.concurrent.ForkJoinTask$AdaptedRunnableAction.exec(ForkJoinTask.java:1407)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Caused by: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: Unsupported features in 4 methods
Detailed message:
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,3,4,6,_,_] stack: [9] locks: [] rethrowException]
Other frame state: [locals: [1,2,3,4,6,_,_] stack: [23] locks: [16 / 7] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:183) [bci: 17, intrinsic: false]
  17: checkcast     #31         // clojure.lang.IFn
  20: aload_0
  21: getfield      #2          // orig:java.lang.Object
  24: aconst_null
  25: getstatic     #5          // java.lang.System.out:java.io.PrintStream

Call path from entry point to clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(Object, Object, Object):
	at clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:166)
	at com.walmartlabs.lacinia.util$inject_enum_transformers$fn__500.invoke(util.clj:123)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,3,4,6,_,_] stack: [9] locks: [] rethrowException]
Other frame state: [locals: [1,2,3,4,6,_,_] stack: [23] locks: [16 / 7] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_capture_BANG___2299.invoke(logging.clj:183) [bci: 17, intrinsic: false]
  17: checkcast     #31         // clojure.lang.IFn
  20: aload_0
  21: getfield      #2          // orig:java.lang.Object
  24: aconst_null
  25: getstatic     #5          // java.lang.System.out:java.io.PrintStream

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,41,_,_,_,_] stack: [44] locks: [] rethrowException]
Other frame state: [locals: [1,2,41,_,_,_,_] stack: [74] locks: [51 / 42] rethrowException]
Parser context: clojure.core.server$io_prepl$fn__8955.invoke(server.clj:287) [bci: 84, intrinsic: false]
  84: dup_x2
  85: if_acmpeq     92
  88: pop
  89: goto          114
  92: swap
  93: pop
  94: dup

Call path from entry point to clojure.core.server$io_prepl$fn__8955.invoke(Object):
	at clojure.core.server$io_prepl$fn__8955.invoke(server.clj:284)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,2,41,_,_,_,_] stack: [44] locks: [] rethrowException]
Other frame state: [locals: [1,2,41,_,_,_,_] stack: [74] locks: [51 / 42] rethrowException]
Parser context: clojure.core.server$io_prepl$fn__8955.invoke(server.clj:287) [bci: 84, intrinsic: false]
  84: dup_x2
  85: if_acmpeq     92
  88: pop
  89: goto          114
  92: swap
  93: pop
  94: dup

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:190) [bci: 15, intrinsic: false]
  15: checkcast     #30         // clojure.lang.IFn
  18: aload_0
  19: getfield      #2          // orig:java.lang.Object
  22: invokeinterface#5, 2       // clojure.lang.IFn.invoke:(java.lang.Object)java.lang.Object

Call path from entry point to clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke():
	at clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:186)
	at clojure.core$pcalls$fn__8483.invoke(core.clj:7042)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,3,_,_,_,_,_,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.tools.logging$fn__2298$log_uncapture_BANG___2302.invoke(logging.clj:190) [bci: 15, intrinsic: false]
  15: checkcast     #30         // clojure.lang.IFn
  18: aload_0
  19: getfield      #2          // orig:java.lang.Object
  22: invokeinterface#5, 2       // clojure.lang.IFn.invoke:(java.lang.Object)java.lang.Object

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)
Error: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:22) [bci: 13, intrinsic: false]
  13: checkcast     #30         // clojure.lang.IFn
  16: getstatic     #5          // const__1:clojure.lang.Var
  19: invokevirtual #4          // clojure.lang.Var.getRawRoot:()java.lang.Object
  22: checkcast     #30         // clojure.lang.IFn

Call path from entry point to clojure.spec.gen.alpha$dynaload$fn__2628.invoke():
	at clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:21)
	at clojure.core$pcalls$fn__8483.invoke(core.clj:7042)
	at clojure.tools.logging.proxy$java.io.ByteArrayOutputStream$ff19274a.flush(Unknown Source)
	at java.io.PrintStream.flush(PrintStream.java:417)
	at com.oracle.svm.jni.functions.JNIFunctions.ExceptionDescribe(JNIFunctions.java:763)
	at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ExceptionDescribe_b5412f7570bccae90b000bc37855f00408b2ad73(generated:0)
Original exception that caused the problem: org.graalvm.compiler.core.common.PermanentBailoutException: Frame states being merged are incompatible: unbalanced monitors - locked objects do not match
 This frame state: [locals: [1,_,_] stack: [6] locks: [] rethrowException]
Other frame state: [locals: [1,_,_] stack: [20] locks: [13 / 4] rethrowException]
Parser context: clojure.spec.gen.alpha$dynaload$fn__2628.invoke(alpha.clj:22) [bci: 13, intrinsic: false]
  13: checkcast     #30         // clojure.lang.IFn
  16: getstatic     #5          // const__1:clojure.lang.Var
  19: invokevirtual #4          // clojure.lang.Var.getRawRoot:()java.lang.Object
  22: checkcast     #30         // clojure.lang.IFn

	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.checkCompatibleWith(FrameStateBuilder.java:415)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.FrameStateBuilder.merge(FrameStateBuilder.java:428)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createTarget(BytecodeParser.java:3185)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.appendGoto(BytecodeParser.java:3358)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.createExceptionDispatch(BytecodeParser.java:3308)
	at jdk.internal.vm.compiler/org.graalvm.compiler.java.BytecodeParser.processBlock(BytecodeParser.java:3241)
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
	at com.oracle.graal.pointsto.DefaultAnalysisPolicy$DefaultVirtualInvokeTypeFlow.onObservedUpdate(DefaultAnalysisPolicy.java:227)
	at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:470)
	at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:542)
	at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:547)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:173)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:183)

	at com.oracle.graal.pointsto.constraints.UnsupportedFeatures.report(UnsupportedFeatures.java:129)
	at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:770)
	... 8 more
Error: Image build request failed with exit status 1
```