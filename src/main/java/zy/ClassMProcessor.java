package zy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.declaration.CtClassImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kaiser_zhao on 2018/8/9.
 */
public class ClassMProcessor extends AbstractProcessor<CtClassImpl> {
    private final static Logger logger = LogManager.getLogger(ClassMProcessor.class);

    private final Map<CtTypeReference, Set<CtTypeReference>> implementors = new HashMap<>();

    public void reportInheritance(CtTypeReference clazz, CtTypeReference superClass) {
        Set<CtTypeReference> subclasses = implementors.get(superClass);
        if (subclasses == null) {
            subclasses = new HashSet<>();
            implementors.put(superClass, subclasses);
        }
        subclasses.add(clazz);
    }

    @Override
    public void process(CtClassImpl clazz) {
        if (clazz.getReference().isAnonymous()) {
            return;
        }
        if (clazz.getSuperclass() != null) {
            reportInheritance(clazz.getReference(), clazz.getSuperclass());
        }
        for (Object o : clazz.getSuperInterfaces()) {
            CtTypeReference superclass = (CtTypeReference) o;
            reportInheritance(clazz.getReference(), superclass);
        }
    }

    Map<CtTypeReference, java.util.Set<CtTypeReference>> executeSpoon(QueueProcessingManager queueProcessingManager, Factory factory){
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(factory.Class().getAll());
        logger.debug("Class Hierarchy: " + implementors);
        return implementors;
    }
}
