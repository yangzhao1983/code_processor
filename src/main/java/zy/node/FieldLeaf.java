package zy.node;

/**
 * Created by kaiser_zhao on 2018/7/27.
 */
public class FieldLeaf extends Leaf{

    public FieldLeaf(boolean isId, String strXpathOrId, String packageName, String className, String name) {
        super(isId, strXpathOrId, packageName, className, name);
        // generate id
        // package.className#name
        id = generateId();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof FieldLeaf)){
            return false;
        }

        FieldLeaf fl = (FieldLeaf)obj;

        // TODO: should consider hwo to create id.
        return this.equals(fl);
    }

    private String generateId(){
        return packageName + "." + className + "#" + name;
    }
}
