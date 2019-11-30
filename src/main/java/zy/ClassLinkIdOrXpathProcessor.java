package zy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import zy.node.Leaf;

import java.util.List;

/**
 * Created by kaiser_zhao on 2018/8/12.
 *
 * This class processor will replace the constants in leaves with actual value.
 *
 * From local leaf, get value of strXpathOrId. It can have two kinds of format:
 * 1. CLASS_NAME.FIELD_NAME
 * 2. FIELD_NAME
 *
 * In case of 2#, CLASS_NAME can be found from leaf
 */
public class ClassLinkIdOrXpathProcessor extends AbstractProcessor<CtClassImpl> {

    private final static Logger logger = LogManager.getLogger(ClassLinkIdOrXpathProcessor.class);

    private List<Leaf> leaves;

    public ClassLinkIdOrXpathProcessor(List<Leaf> leaves){
        this.leaves = leaves;
    }

    @Override
    public void process(CtClassImpl clazz) {

        if (clazz.getReference().isAnonymous()) {
            return;
        }

        String packageName = clazz.getPackage().getQualifiedName();

        String className = clazz.getSimpleName();

        // get constants
        List<CtFieldImpl> list = clazz.getValueByRole(CtRole.FIELD);
        list.stream().filter(x->x.getType().toString().equals("java.lang.String")).forEach(y->{

            List<CtElement> elems = y.getElements(new AbstractFilter<CtElement>(CtElement.class) {
                @Override
                public boolean matches(CtElement ctElement) {
                    return ctElement instanceof CtLiteral;
                }
            });


            if (elems.size()>0) {

                // Should check if "String x = null"
                String val = ((CtLiteral)elems.get(0)).getValue()== null ? "":((CtLiteral)elems.get(0)).getValue().toString();

                String varName = y.getSimpleName();
                leaves.forEach(l->replaceIdOrXpath(l, packageName, className,varName,val));
            }
        });
    }

    public void executeSpoon(QueueProcessingManager queueProcessingManager, Factory factory){
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(factory.Class().getAll());
    }

    private void replaceIdOrXpath(Leaf leaf, String packageName, String className, String varName, String value){
        String srcXpathOrId = leaf.getStrXpathOrId();

        if(!srcXpathOrId.contains("\"")){
            System.out.println("no \"==========" + srcXpathOrId);
        }
        String literalValue = varName;
        if(srcXpathOrId.contains(".")){
            literalValue = packageName + "." + className + "." + literalValue;
        }
        if(srcXpathOrId.equals(literalValue)){
            leaf.setStrXpathOrId(value);
        }
    }
}
