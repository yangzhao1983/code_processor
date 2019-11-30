package zy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by kaiser_zhao on 2018/8/6.
 */
public class TestLogger {
//    static {
//        String logPropFilePath = System.getProperty("log4j.configurationFile");
//        if(StringUtil.isBlank(logPropFilePath)){
//            System.setProperty("log4j.configurationFile","/Users/kaiser_zhao/Work/projects/code-processor/src/main/resources/log4j2.xml");
//        }
//    }
    private static Logger logger = LogManager.getLogger(TestLogger.class);
    public final static void main(String... strings){
        logger.error("========");
    }
}
