package zy;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.QueueProcessingManager;
import zy.node.ByNodeFactory;
import zy.node.Leaf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaiser_zhao on 08/07/2018.
 */
public class ClassProcessor extends AbstractProcessor<CtClass> {
    public static List<Leaf> leaves = new ArrayList<Leaf>();

    public void process(CtClass element) {
        String clazzPath = element.getPath().toString();

        //TODO: only for idcs identity
//        if (!clazzPath.startsWith("#subPackage[name=oracle]#subPackage[name=idaas]#subPackage[name=ui]#subPackage[name=lib]#subPackage[name=identity]")) {
//            return;
//        }
        element.getElements(new AbstractFilter<CtInvocation>(CtInvocation.class) {

            @Override
            public boolean matches(CtInvocation invocation) {
                String invocationString = invocation.toString();

                if (invocationString.startsWith("org.openqa.selenium.By.id") || invocationString.startsWith("org.openqa.selenium.By.xpath")) {
                    //"Field By parameter" is identified by package+class+name. Property is boolean xpath/id and string xpath/id.
                    //"local variable By" is identified by name. It is part of method.
                    System.out.println("invocationString================" + invocationString);
                    Leaf l = ByNodeFactory.getLeaf(invocation);
                    if (l != null) {
                        leaves.add(l);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    List<Leaf> executeSpoon(QueueProcessingManager queueProcessingManager, Factory factory){
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(factory.Class().getAll());
        return leaves;
    }

}