package zy;

import org.openqa.selenium.By;

/**
 * Created by kaiser_zhao on 6/26/18.
 */
public class TestProcessor2 {
    private static final String NOT_LITERAL = "id";
    private static By byField = By.id(NOT_LITERAL);

    public String tpM2(){
        tpM3();
        System.out.println(byField);
        return "";
    }

    public String tpM3(){
        By byStackParam = By.id("gogogogo");
        tpM4();
        return "";
    }

    public void tpM4(){

    }
}
