package zy.node;

import spoon.reflect.reference.CtExecutableReference;
import zy.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kaiser_zhao on 6/19/18.
 *
 * Method,
 * 1. id
 * It is identified by package.class.name(parameter).
 *
 * 2. Contains sub methods.
 * List<CtExecutableReference>
 *
 * 3. Contains By Fileds
 * List<FieldLeaf>: generated in MethodProcessor
 *
 * 4. Contains By Variable.
 * List<LocalLeaf>: When MethodProcessor is executed, it is empty. It is filled after ClassProcessor is executed.
 *
 */
public class MethodNode{

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MethodNode){
            MethodNode target = (MethodNode)obj;
            return this.getName().equals(((MethodNode) obj).getName());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public CtExecutableReference getMethod() {
        return method;
    }

    private CtExecutableReference method;

    private boolean isAPIMethod;

    private Set<MethodNode> subMethods = new HashSet<>();

    private List<FieldLeaf> fieldLeaves = new ArrayList<>();

    public List<FieldLeaf> getFieldLeaves() {
        return fieldLeaves;
    }

    private Set<LocalLeaf> localLeaves = new HashSet<>();

    public void addFiledLeavesChild(FieldLeaf fieldLeaf){
        fieldLeaves.add(fieldLeaf);
    }

    public void addLocalLeaf(LocalLeaf lf){
        localLeaves.add(lf);
    }

    public void seedFieldLeafProp(FieldLeaf fieldLeaf){
        FieldLeaf[] toBeSeeded = fieldLeaves.stream().filter(x->x.getId().equals(fieldLeaf.getId())).toArray(FieldLeaf[]::new);
        if(toBeSeeded!=null && toBeSeeded.length>=0){
            toBeSeeded[0].setId(fieldLeaf.isId);
            toBeSeeded[0].setStrXpathOrId(fieldLeaf.getStrXpathOrId());
        }
    }

    public Set<LocalLeaf> getLocalLeaves() {
        return localLeaves;
    }

    public void addMethodRefChild(MethodNode ref){
        boolean hit = false;
        for(MethodNode mn : subMethods){
            if(mn.getName().equals(ref.getName())){
                mn.getMethodRefs().addAll(ref.getMethodRefs());
                mn.getFieldLeaves().addAll(ref.getFieldLeaves());
                mn.getLocalLeaves().addAll(ref.getLocalLeaves());
                hit = true;
                break;
            }
        }
        if(!hit){
            subMethods.add(ref);
        }
    }

    public Set<MethodNode> getMethodRefs(){
        return subMethods;
    }

    public String getName() {
        return name;
    }

    private String name;

    public MethodNode(CtExecutableReference method){
        this.method = method;


        String signature = method.getSignature();
        String mDeclare = null;
        try {
            mDeclare = method.getDeclaringType().toString();
        } catch (Exception e) {
            mDeclare = "";
        } finally {
        }

        name = mDeclare + "." + signature;
        String returnType = null;

        // TODO: if type == null, why?
        if(method.getType() == null){
            returnType = "";
        }else{
            returnType = method.getType().toString();
        }

        if(!Constants.API_METHOD_TRETURN_TYPE.equals(returnType)){
            isAPIMethod = false;
            return;
        }

        if(method.getParameters().size() != 1){
            isAPIMethod = false;
            return;
        }

        if(method.getParameters().get(0).toString().startsWith(Constants.API_METHOD_PARAM_TYPE)){
            isAPIMethod = true;
        }
    }

    public boolean isAPIMethod(){
        return isAPIMethod;
    }
}
