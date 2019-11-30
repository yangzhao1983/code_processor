package zy.builder;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import zy.node.FieldLeaf;
import zy.node.Leaf;
import zy.node.MethodNode;

import java.util.*;

/**
 * Created by kaiser_zhao on 2018/8/11.
 */
public class MethodCallHierarchyBuilder {

    public MethodCallHierarchyBuilder(Map<CtTypeReference, Set<CtTypeReference>> classHierarchy, List<MethodNode> callList,
                                      List<Leaf> leaves
                                      ){
        this.classHierarchy = classHierarchy;
        this.callList = callList;
        this.leaves =leaves;
    }

    private MethodNode methodNode;
    private List<MethodNode> callList;
    private Map<CtTypeReference, Set<CtTypeReference>> classHierarchy;
    private List<Leaf> leaves;

    private MethodNode methodNameExistInMethodNodeList(String methodName){

        if(callList == null || callList.size() == 0){
            return null;
        }else{
            MethodNode[] mnArray = callList.stream().filter(x->x.getName().equals(methodName)).toArray(MethodNode[]::new);
            if(mnArray==null || mnArray.length ==0){
                return null;
            }
            return mnArray[0];
        }
    }

    public void fillMethodNodeList(MethodNode mn){
        fillMethodNodeList(mn.getName(), new ArrayList<String>(), mn);
    }

    /**
     * There are two lists.
     *
     * 1.
     *
     * @param methodName
     * @param alreadyVisited
     * @param parent
     */
    private void fillMethodNodeList(String methodName, List<String> alreadyVisited, MethodNode parent){
        // if methodName can be found in alreadyVisited, return
        if(alreadyVisited.contains(methodName)){
            return;
        }

        if(methodName.contains("m(HashMap")){
            System.out.println("=======");
        }

        // if methodName is not in mns, return TODO: why
        MethodNode mn = methodNameExistInMethodNodeList(methodName);
        if(mn == null){
            return;
        }

        // 1. get sub method node list for method name.
        Set<MethodNode> subMns = mn.getMethodRefs();

        // put all of the sub methods of "methodName" to parent
        if(parent!=null){
            for(MethodNode tmn : subMns) {
                parent.getMethodRefs().add(tmn);
            }
            parent.getFieldLeaves().addAll(mn.getFieldLeaves());
            parent.getLocalLeaves().addAll(mn.getLocalLeaves());
        }



        // 2. fill method node with field leaves
        // fillMethodNodeWithFieldLeaves(mn);

        // 3. add method node to already visited
        alreadyVisited.add(methodName);

        // 4.1 if sub method node list of parent is empty return.
        Set<MethodNode> pmns  = parent.getMethodRefs();

        if (pmns.size() == 0) {
            return;
        } else {
            // 4.2 else get sub method node list.
            Iterator<MethodNode> iter = pmns.iterator();
            while(iter.hasNext()){
                MethodNode x = iter.next();

                if(x.getName().contains("pM(")){
                    System.out.println("=======");
                }
                fillMethodNodeList(x.getName(), alreadyVisited, x);
                Set<CtTypeReference> subclasses = classHierarchy.get(x.getMethod().getDeclaringType());
                if (subclasses != null) {
                    for (CtTypeReference subclass : subclasses) {
                        CtExecutableReference reference = x.getMethod().getOverridingExecutable(subclass);

                        if (reference != null) {
                            MethodNode orMn = new MethodNode(reference);
                            fillMethodNodeList(orMn.getName(), alreadyVisited, x);
                        }
                    }
                }
            }
            // for each sub method node, call fillMethodNodeList(String methodName, List<MethodNode> mns, List<MethodNode> alreadyVisited)

            //      for each sub method node,
            //          for each sub class, get overriding methods.
            //              for each overriding methods, call fillMethodNodeList(String methodName, List<MethodNode> mns, List<MethodNode> alreadyVisited)
        }
    }

    private void fillMethodNodeWithFieldLeaves(MethodNode mn){
        leaves.stream().filter(x-> x instanceof FieldLeaf).forEach(
                x->{
                    for(FieldLeaf fl : mn.getFieldLeaves()){
                        if (fl.getId().equals(x.getId())){
                            fl.setId(x.isId());
                            fl.setStrXpathOrId(x.getStrXpathOrId());
                        }
                    }
                }
        );
    }

    private List<MethodNode> findExecutablesForMethodName(String methodName, List<MethodNode> callList) {
        ArrayList<MethodNode> result = new ArrayList<>();
        for (MethodNode md : callList) {
            String executableReferenceMethodName = md.getName();
            if (executableReferenceMethodName.equals(methodName)) {
                result.add(md);
            }
        }
        return result;
    }
}
