package zy.node;

/**
 * Created by kaiser_zhao on 08/07/2018.
 */
public abstract class Leaf {

    protected String id;

    public String getId() {
        return id;
    }

    // The leaf is id/xpath, true/false.
    protected boolean isId = false;

    // The string parameter of this leaf
    // If isId = true, then this is the id of the By element.
    // Else if isId=false, then this is the xpath of the By element.
    protected String strXpathOrId = "";

    protected String packageName = "";

    protected String className = "";

    protected String name = "";

    public Leaf(boolean isId, String strXpathOrId, String packageName, String className, String name){
        this.isId = isId;
        this.strXpathOrId = strXpathOrId;
        this.packageName = packageName;
        this.className = className;
        this.name = name;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean id) {
        isId = id;
    }

    public void setStrXpathOrId(String strXpathOrId) {
        this.strXpathOrId = strXpathOrId;
    }

    public String getStrXpathOrId() {
        return strXpathOrId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }
}
