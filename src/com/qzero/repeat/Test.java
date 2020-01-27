package com.qzero.repeat;

import java.io.File;
import java.util.List;
import java.util.Stack;

public class Test {

    public static void main(String[] args) throws Exception {
        //IdiomLoadUtils.processTranslated(new File("H:\\forFun\\translated.txt"),new File("H:\\forFun\\translatedProcessed.txt"));
        //IdiomLoadUtils.translation(new File("H:\\forFun\\成语字典表\\成语字典表.txt"),new File("H:\\forFun\\translated.txt"));

        File in=new File("H:\\forFun\\translatedProcessed.txt");
        long start=System.currentTimeMillis();

        //IdiomSeeker seeker=new IdiomSeeker();
        /*Idiom idiom;
        while ((idiom=seeker.getIdiom())!=null){
            if(IdiomLoadUtils.isRepeatable(idiom)){
                System.out.println(idiom);
            }
            seeker.nextLine();
        }*/

        List<Idiom> result=IdiomUtils.getRepeatablePath(in,"lao");
        if(result!=null){
            for(Idiom idiom:result){
                System.out.println(idiom);
            }
        }


        long end=System.currentTimeMillis();
        System.out.println("Time:"+(end-start)+"ms");
    }

}
