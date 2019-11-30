package zy.node;

/**
 * Created by kaiser_zhao on 2018/7/27.
 *
 * id = methodName.name
 */
public class LocalLeaf extends Leaf{

    @Override
    public int hashCode() {

        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LocalLeaf){
            return this.getId().equals(((LocalLeaf)obj).getId());
        }
        return super.equals(obj);
    }

    private String methodName = "";
    public LocalLeaf(boolean isId, String strXpathOrId, String packageName, String className, String name, String methodName) {
        super(isId, strXpathOrId, packageName, className, name);
        this.methodName = methodName;
        this.id = methodName + "." + name + "." + strXpathOrId + "." + (isId? "id" : "xpath");
    }

    public String getMethodName() {
        return methodName;
    }
}
