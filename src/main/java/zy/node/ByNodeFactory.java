package zy.node;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.path.CtRole;

/**
 * Created by kaiser_zhao on 2018/7/27.
 */
public class ByNodeFactory {

    private static Logger logger = LogManager.getLogger(ByNodeFactory.class);

    public static Leaf getLeaf(CtInvocation invocation){

        String invocationString = invocation.toString();

        String parentClass = invocation.getParent().getClass().toString();

        // is id or path
        boolean isId = invocationString.startsWith("org.openqa.selenium.By.id");

        // id xpath
        int parenthesesStartIndex = invocationString.indexOf("(");
        String idXpath = invocationString.substring(parenthesesStartIndex+1, invocationString.length()-1);

        // package
        String path = invocation.getPath().toString();

        String packageName = getPackageName(path);

        // class
        int cNameSIndex = path.indexOf("containedType[name=") + "containedType[name=".length();
        int cNameEIndex = path.indexOf("]#typeMember");
        String className = path.substring(cNameSIndex, cNameEIndex);



        boolean isLocalBy = parentClass.endsWith("CtLocalVariableImpl") ||
                parentClass.endsWith("CtInvocationImpl");

        // local by or filed by
        if(parentClass.endsWith("spoon.support.reflect.code.CtAssignmentImpl")) {

            String clazzName = invocation.getParent().getValueByRole(CtRole.ASSIGNED).getClass().toString();
            if(clazzName.endsWith("CtVariableWriteImpl")){
                isLocalBy = true;
            }
        }

        // name
        String name = "";
        if(parentClass.endsWith("CtFieldImpl") || parentClass.endsWith("CtLocalVariableImpl")){
            String pString = invocation.getParent().toString();
            int startIndex = pString.indexOf("org.openqa.selenium.By") + "org.openqa.selenium.By".length() + 1;
            int endIndex = pString.indexOf(" = org.openqa.selenium.By");
            name = pString.substring(startIndex, endIndex);
        }

        if(parentClass.endsWith("CtAssignmentImpl")){

            String pString = invocation.getParent().toString();
            System.out.println("pString==================" + pString);
            int endIndex = pString.indexOf("=");
            name = pString.substring(0, endIndex-1);
        }

        if(name.equals("localBy")){
            System.out.println("");
        }

        // method
        if(isLocalBy){

            // get method name package.class.signature
            CtElement parentElem = invocation.getParent(CtMethod.class);
            String signature = null;
            try {
                signature = ((CtMethod) parentElem).getReference().getSignature();
            } catch (Exception e) {
                //e.printStackTrace();
                //TODO: can not deal with the by, if it is in construct method
                return null;

            }
            String mDeclare = ((CtMethod) parentElem).getReference().getDeclaringType().toString();
            String mName = mDeclare + "." + signature;
            return new LocalLeaf(isId, idXpath, packageName, className, name, mName);
        }else{
            return new FieldLeaf(isId, idXpath, packageName, className, name);
        }
    }

    private static String getPackageName(String path){
        String packageName = path.replaceAll("]#subPackage\\[name=", ".").replace("#subPackage[name=", "").replaceAll("].*", "");
        return packageName;
    }
}
