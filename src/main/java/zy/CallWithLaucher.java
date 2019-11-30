package zy;


import spoon.Launcher;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.QueueProcessingManager;
import spoon.support.compiler.FileSystemFolder;
import zy.builder.APIMethodLeavesMapBuilder;
import zy.builder.MethodCallHierarchyBuilder;
import zy.node.Leaf;
import zy.node.MethodNode;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kaiser_zhao on 2018/8/9.
 */
public class CallWithLaucher {

    public final static void main(String... strings){
        Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--source-classpath", "./build/libs/code-processor-1.0-SNAPSHOT.jar" +
                ":lib/cglib-nodep-2.1_3.jar:lib/commons-codec-1.10.jar:lib/commons-collections-3.2.2.jar:lib/commons-exec-1.3.jar" +
                ":lib/commons-io-2.4.jar:lib/commons-lang-2.3.jar:lib/commons-lang3-3.4.jar:lib/commons-logging-1.2.jar:lib/cssparser-0.9.18.jar" +
                ":lib/gson-2.3.1.jar:lib/guava-19.0.jar:lib/htmlunit-2.21.jar:lib/htmlunit-core-js-2.17.jar:lib/htmlunit-driver-2.21.jar" +
                ":lib/httpclient-4.5.2.jar:lib/httpcore-4.4.4.jar:lib/httpmime-4.5.2.jar:lib/InMemoryJavaCompiler-1.3.0.jar" +
                ":lib/jetty-io-9.2.15.v20160210.jar:lib/jetty-util-9.2.15.v20160210.jar:lib/jna-4.1.0.jar:lib/jna-platform-4.1.0.jar" +
                ":lib/jsap-2.1.jar:lib/log4j-1.2.17.jar:lib/neko-htmlunit-2.21.jar:lib/netty-3.5.7.Final.jar:lib/org.eclipse.jdt.core-3.12.0.v20160516-2131.jar" +
                ":lib/sac-1.3.jar:lib/selenium-api-2.53.1.jar:lib/selenium-chrome-driver-2.53.1.jar:lib/selenium-edge-driver-2.53.1.jar" +
                ":lib/selenium-firefox-driver-2.53.1.jar:lib/selenium-ie-driver-2.53.1.jar:lib/selenium-java-2.53.1.jar:lib/selenium-leg-rc-2.53.1.jar" +
                ":lib/selenium-remote-driver-2.53.1.jar:lib/selenium-safari-driver-2.53.1.jar:lib/selenium-support-2.53.1.jar:lib/serializer-2.7.2.jar" +
                ":lib/slf4j-api-1.7.5.jar:lib/spoon-core-5.9.0.jar:lib/spoon-core-6.2.0-jar-with-dependencies.jar:lib/websocket-api-9.2.15.v20160210.jar" +
                ":lib/websocket-client-9.2.15.v20160210.jar:lib/websocket-common-9.2.15.v20160210.jar:lib/xalan-2.7.2.jar" +
                ":lib/xercesImpl-2.11.0.jar:lib/xml-apis-1.4.01.jar"});
        launcher.addInputResource(new FileSystemFolder(
                new File("/Users/kaiser_zhao/Work/idcs/code/idaas-integration-tests/src/test/java/oracle/idaas/ui/lib/")));
       // launcher.addInputResource(new FileSystemFolder(
       //         new File("/Users/kaiser_zhao/Work/projects/code-processor")));
        try {
            launcher.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // do sth
        doSth(launcher);
    }

    /**
     * 1. get list of leaves
     * 2. get hierarchy of class
     * 3. get the list of methods
     *  3.1 get FieldLeaf from existing list for not only top-level method, but also their sub methods
     * 4. Create the caller chain for API methods
     *
     *
     * @param launcher
     */
    private static void doSth(Launcher launcher){
        Factory factory = launcher.getFactory();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(factory);

        // get list of leaves
        List<Leaf> leafList = new ClassProcessor().executeSpoon(queueProcessingManager, factory);

        // get hierarchy of class
        QueueProcessingManager queueProcessingManager1 = new QueueProcessingManager(factory);
        Map<CtTypeReference, Set<CtTypeReference>> classHierarchy = new ClassMProcessor().executeSpoon(queueProcessingManager1, factory);

        // get the list of methods
        // MethodRefProcessor takes list of leaves as constructor argument, which can be used to seed the field of the methodNode,
//        queueProcessingManager.addProcessor(new MethodRefProcessor(leafList));
        List<MethodNode> callList =
                new MethodRefProcessor(leafList).executeSpoon(queueProcessingManager, factory);
        System.out.println(callList.size());
        MethodNode[] array = callList.stream().filter(x->x.isAPIMethod()).toArray(MethodNode[]::new);


        // fill the sub method list for every method in the tree starting with APIMethod
        MethodCallHierarchyBuilder mchb = new MethodCallHierarchyBuilder(classHierarchy, callList, leafList);
        callList.stream().filter(x->x.isAPIMethod()).forEach(x->mchb.fillMethodNodeList(x));
        System.out.println(callList.size());
        MethodNode[] array2 = callList.stream().filter(x->x.isAPIMethod()).toArray(MethodNode[]::new);
        System.out.println(array2.length);

        // add local leaf to methods
        //MethodLocalLeafHierarchyBuilder mllhb = new MethodLocalLeafHierarchyBuilder(callList, leafList);
        //mllhb.boundLocalLeaf2Methods();

        QueueProcessingManager queueProcessingManager2 = new QueueProcessingManager(factory);
        new ClassLinkIdOrXpathProcessor(leafList).executeSpoon(queueProcessingManager2, factory);

        System.out.println(callList.size());
        for(MethodNode mn : callList){
            if(mn.getName().equals("oracle.idaas.ui.lib.identity.jobs.Child.pM()")){
                System.out.println("=================oracle.idaas.ui.lib.identity.jobs.Child.pM()");
            }
        }
        // Generate the map,
        // key is the name of the API Method identity.LoginTenantAdminPage.methodName
        // value is the Set of leaves
        Map<String, Set<Leaf>> pairs = new APIMethodLeavesMapBuilder(callList).getAPIMethodLeavesMap();

//        for(MethodNode mn : callList){
//            if(mn.getName().contains("activateUser")){
//                System.out.println("activateUser======");
//            }
//            if(mn.getName().contains("userDetailsActivateButtonLocator")){
//                System.out.println("userDetailsActivateButtonLocator======");
//            }
//        }

        Set<String> allApis = new HashSet<>();
        for(String apiM : pairs.keySet()){
            for(Leaf lf : pairs.get(apiM)){
                if(lf.getStrXpathOrId().contains("opaas-user-menu-button")) {
                    System.out.println(apiM + "========>id=opaas-user-menu-button");
                    allApis.add(apiM);
                }
            }
        }

        System.out.println();
        allApis.stream().forEach(x->System.out.println(x));
    }
}
