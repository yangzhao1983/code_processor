package zy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;

/**
 * Created by kaiser_zhao on 18/6/7.
 */
public class TestProcessor {

    private static final String NOT_LITERAL = "id";
    private static By byField = By.id(NOT_LITERAL);

    public static final void main(String...strings){

//        By later;
//        System.out.println("======");
//        By byStackParam = By.id(NOT_LITERAL);
//        System.out.println(byField);
//        By localBy = byField;
//        later = By.id("id");
    }

    //TODO: API method
    // 1. return webdriver
    // 2. with parameter hash<String, String>
    public WebDriver apiMethod(HashMap<String,String> creds){
        System.out.println(byField);
        System.out.println(By.id("id"));
        By localBy = By.id("id");
        method1();
        TestProcessor2 tp2 = new TestProcessor2();
        tp2.tpM2();
        return null;
    }

    public void method1(){

    }
}
