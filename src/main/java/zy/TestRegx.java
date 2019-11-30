package zy;

/**
 * Created by kaiser_zhao on 2018/8/9.
 */
public class TestRegx {

    public final static void main(String... strings){
        String s = "#subPackage[name=oracle]#subPackage[name=idaas]#subPackage[name=ui]#subPackage[name=lib]#subPackage[name=identity]#subPackage[name=usermgmt]#containedType[name=UserPage]#typeMember[index";
        String packageName = s.replaceAll("]#subPackage\\[name=", ".").replace("#subPackage[name=", "").replaceAll("].*", "");
        System.out.println(packageName);
    }
}
