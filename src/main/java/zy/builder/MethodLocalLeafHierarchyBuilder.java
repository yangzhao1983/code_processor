package zy.builder;

import zy.node.Leaf;
import zy.node.LocalLeaf;
import zy.node.MethodNode;

import java.util.List;
import java.util.Set;

/**
 * Created by kaiser_zhao on 2018/8/11.
 */
public class MethodLocalLeafHierarchyBuilder {
    private List<MethodNode> callList;
    private List<Leaf> leaves;
    public MethodLocalLeafHierarchyBuilder(List<MethodNode> callList,
                                           List<Leaf> leaves){
        this.callList = callList;
        this.leaves = leaves;
    }

    public void boundLocalLeaf2Methods(){
        // traverse
        callList.stream().forEach(x->traverseMethodTree(x));
    }

    private void traverseMethodTree(MethodNode mn){

        Leaf[] localLeaves = leaves.stream().filter(x->x instanceof LocalLeaf).filter(x->{
            LocalLeaf lf = (LocalLeaf)x;
            return lf.getMethodName().equals(mn.getName());
        }).toArray(Leaf[]::new);

        mn.addLocalLeaf((LocalLeaf)localLeaves[0]);

        Set<MethodNode> mnList = mn.getMethodRefs();
        if(mnList == null || mnList.size()==0){
            return;
        }else{
            mnList.stream().forEach(x->traverseMethodTree(x));
        }
    }
}
