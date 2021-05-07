package org.akycorp.build.analyser;

/*
 * @author  : ajay_kumar_yadav
 * @created : 07/05/21
 */
public class AppSetting {
    private static int DEFAULT_FLAG_TIME = 4;

    public static void setMinBuildTimeForFlag(int minFlagValue){
        if(minFlagValue >= 1) {
            DEFAULT_FLAG_TIME = minFlagValue;
        }else{
            throw new IllegalArgumentException("value can not be less than 1");
        }
    }

    public static int getMinBuildTimeForFlag(){
        return DEFAULT_FLAG_TIME;
    }
}
