package zy.builder;

import zy.node.Leaf;
import zy.node.MethodNode;

import java.util.*;

/**
 * Created by kaiser_zhao on 2018/8/12.
 */
public class APIMethodLeavesMapBuilder {

    private List<MethodNode> callList;
    private Map<String, Set<Leaf>> apiMethodLeavesMap = new HashMap<>();

    public APIMethodLeavesMapBuilder(List<MethodNode> callList){
        this.callList = callList;
    }

    public Map<String, Set<Leaf>> getAPIMethodLeavesMap(){
        MethodNode[] array = callList.stream().filter(x->x.isAPIMethod()).toArray(MethodNode[]::new);
        callList.stream().filter(x->x.isAPIMethod()).forEach(x->{
            Set<Leaf> resLeaves = new HashSet<>();
            List<String> visited = new ArrayList<>();
            traverse(x, resLeaves, visited);
            apiMethodLeavesMap.put(x.getName(),resLeaves);
        });
        return apiMethodLeavesMap;
    }

    private void traverse(MethodNode methodNode, Set<Leaf> resLeaves, List<String> visited){
        if(visited.contains(methodNode.getName())){
            return;
        }else{
            visited.add(methodNode.getName());
        }

        resLeaves.addAll(methodNode.getFieldLeaves());
        resLeaves.addAll(methodNode.getLocalLeaves());
        methodNode.getMethodRefs().stream().forEach(x->traverse(x,resLeaves,visited));
    }
}
