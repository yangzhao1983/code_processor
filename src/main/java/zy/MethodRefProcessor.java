package zy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.CtMethodImpl;
import zy.node.FieldLeaf;
import zy.node.Leaf;
import zy.node.LocalLeaf;
import zy.node.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaiser_zhao on 2018/8/9.
 */
public class MethodRefProcessor extends AbstractProcessor<CtMethodImpl> {

    private List<MethodNode> callList = new ArrayList<>();
    private static Logger logger = LogManager.getLogger(MethodRefProcessor.class);
    private List<Leaf> leaves;

    public MethodRefProcessor(List<Leaf> leaves){
        this.leaves = leaves;
    }

    @Override
    public void process(CtMethodImpl ctMethod) {

        MethodNode mn = new MethodNode(ctMethod.getReference());
        System.out.println("signature : " + ctMethod.getReference().getSignature());


        String signature = ctMethod.getReference().getSignature();
        String s = ctMethod.getReference().getDeclaringType().toString();
        List<CtElement> elements = ctMethod.getElements(new AbstractFilter<CtElement>(CtElement.class) {
            @Override
            public boolean matches(CtElement ctElement) {
                if(ctElement instanceof CtAbstractInvocation){
                    CtAbstractInvocation ai = (CtAbstractInvocation)ctElement;

                    // only get the "field read", since the "variable read" relationship should has recorded in the
                    // leaf
                    ai.getArguments().stream().filter(x->{

                        if(! (x instanceof CtVariableRead)){
                            return false;
                        }
                        CtVariableRead ctvr = (CtVariableRead)x;

                        // TODO: filter item as msgs.length
                        String ctvrType = ctvr.getType()==null? "" : ctvr.getType().toString();

                        return ctvrType.equals("org.openqa.selenium.By");
                    }).forEach(x -> {
                        logger.info("argument is " + x);

                        if (x instanceof CtFieldRead) {
                            logger.info("is CtFieldRead");
                            // should record it as leaf of the method
                            // argument's id = package+class+name
                            // getQualifiedName  = package.class#name
                            CtFieldRead ctfr = (CtFieldRead)x;
                            String qualifiedName = ctfr.getVariable().getQualifiedName();
                            logger.info("getQualifiedName " + qualifiedName);
                            mn.addFiledLeavesChild(generateFieldLeaf(qualifiedName));
                        }
                    });

                }
                return ctElement instanceof CtAbstractInvocation;
            }
        });

        // fill the sub methods of the current method
        for (CtElement element : elements) {
            CtAbstractInvocation invocation = (CtAbstractInvocation) element;
            mn.getMethodRefs().add(new MethodNode(invocation.getExecutable()));
            //mn.addMethodRefChild(new MethodNode(invocation.getExecutable()));
        }

        // fill the local leaves of the current method
        fillLocalLeaves(mn);

        callList.add(mn);
    }

    private void fillLocalLeaves(MethodNode mn){
        if(mn.getName().contains("userDetailsActivateButtonLocator")){
            System.out.println("=======");
        }
        List<Leaf> leavesTmp = leaves;
        leaves.stream().filter(x->(x instanceof LocalLeaf)).forEach(x->{
            LocalLeaf lf = (LocalLeaf)x;
            if(lf.getMethodName().equals(mn.getName())){
                mn.addLocalLeaf(lf);
            }
        });
    }

    public List<MethodNode> executeSpoon(QueueProcessingManager queueProcessingManager, Factory factory){
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(factory.Class().getAll());
        return callList;
    }

    /**
     * qualifiedName is in form of package.class#name
     *
     * @param qualifiedName
     * @return
     */
    private FieldLeaf generateFieldLeaf(String qualifiedName){
        int indexLastPeriod = qualifiedName.lastIndexOf(".");
        int indexPound = qualifiedName.indexOf("#");
        String packageName = qualifiedName.substring(0, indexLastPeriod);
        String className = qualifiedName.substring(indexLastPeriod+1, indexPound);
        String name = qualifiedName.substring(indexPound+1);

        FieldLeaf fl = getFieldLeafFromExistingList(packageName, className, name);

        if(fl!=null){
            return fl;
        }
        return new FieldLeaf(false, "", packageName, className, name);
    }

    private FieldLeaf getFieldLeafFromExistingList(String packageName, String className, String fieldName){
        FieldLeaf res = null;
        for(Leaf fl : leaves){
            if(fl instanceof FieldLeaf){
                if(fl.getPackageName().equals(packageName) &&
                        fl.getClassName().equals(className) &&
                        fl.getName().equals(fieldName)){
                    res = (FieldLeaf)fl;
                }
            }
        }
        return res;
    }
}

