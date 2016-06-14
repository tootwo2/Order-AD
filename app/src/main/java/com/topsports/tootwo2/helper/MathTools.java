package com.topsports.tootwo2.helper;

/**
 * Created by tootwo2 on 16/2/23.
 * 数学计算工具类
 */
public class MathTools {

    public static int safePerDivideP(int a1,int a2){
        int d=0;
        if(a2==0){
            d=0;
        }else{
            d=a1*100/a2;
        }
        return d;
    }

    public static double safePerDivide(int a1,int a2){
        double d=0;
        if(a2==0){
            d=0;
        }else{
            d=((double)a1)/((double)a2);
        }
        return d;
    }
}
