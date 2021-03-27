package ru.nsu.fit.ojp.ushaev.ojp_3;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class MyJavaAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Hello from Java Agent!");

        // Transformer to modify bytecode using Javassist
        inst.addTransformer(new MyTransformer());

        // Shutdown Hook to print number of loaded classes in the end of the program
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                System.out.println("Number of loaded classes: " + inst.getAllLoadedClasses().length)));
    }

    static class MyTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {

            if (className.equals("TransactionProcessor")) {
                try {

                    ClassPool cp = ClassPool.getDefault();
                    CtClass cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

                    // Modify processTransaction metod - add 99 to first argument
                    CtMethod mProcessTransaction = cc.getDeclaredMethod("processTransaction");
                    mProcessTransaction.insertBefore("{$1 += 99;}");
                    mProcessTransaction.addLocalVariable("startTime", CtClass.longType);
                    String methodName = mProcessTransaction.getLongName();
                    mProcessTransaction.insertBefore("{startTime = System.currentTimeMillis();}");
                    mProcessTransaction.insertAfter(
                            "{ru.nsu.fit.ojp.ushaev.ojp_3.TimeRegistry.instance.addMetric(\"" + methodName + "\", System.currentTimeMillis() - startTime);}");


                    // Visit method main
                    CtMethod main = cc.getDeclaredMethod("main");
                    main.insertAfter("{System.out.println(ru.nsu.fit.ojp.ushaev.ojp_3.TimeRegistry.instance.getMinTime(\"" + methodName + "\"));}");
                    main.insertAfter("{System.out.println(ru.nsu.fit.ojp.ushaev.ojp_3.TimeRegistry.instance.getAvgTime(\"" + methodName + "\"));}");
                    main.insertAfter("{System.out.println(ru.nsu.fit.ojp.ushaev.ojp_3.TimeRegistry.instance.getMaxTime(\"" + methodName + "\"));}");

                    // Get bytecode
                    byte[] modifiedClassfileBuffer = cc.toBytecode();
                    cc.detach();
                    return modifiedClassfileBuffer;

                } catch (IOException | NotFoundException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }
            return classfileBuffer;
        }
    }
}
