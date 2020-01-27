package com.qzero.repeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class MenuMain {

    private static final String FILE_PATH_DEFAULT = "translatedProcessed.txt";

    public static void main(String[] args) throws Exception {
        System.out.print("请输入编码后的成语字典文件路径（默认为" + FILE_PATH_DEFAULT + "）:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String filePath = reader.readLine();
        if (filePath == null || filePath.length() == 0)
            filePath = FILE_PATH_DEFAULT;

        File in = new File(filePath);
        if (!in.exists()) {
            System.out.println("错误，文件不存在");
            return;
        }

        while (true) {
            System.out.print("\n");
            System.out.println("1)根据上一个结尾读音寻找下一个接龙成语");
            System.out.println("2)根据上一个结尾读音寻找一条复读路径");
            System.out.println("3)Good good bye");
            System.out.print("请输入操作:");
            String choice = reader.readLine();

            if (choice.equals("3"))
                break;


            switch (choice) {
                case "1":
                    next(in);
                    break;
                case "2":
                    repeat(in);
                    break;
                default:
                    System.out.println("非法输入");
                    break;
            }
        }

        System.out.println("Good bye");

    }

    private static void next(File in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("请输入上一个结尾读音：");
        String startPronunciation = reader.readLine();

        Idiom nextIdiom = IdiomUtils.getNextIdiom(in, startPronunciation);
        if (nextIdiom == null) {
            System.out.println("未找到结果");
        } else {
            System.out.println("结果如下\n\n---------------------------------");
            System.out.println(nextIdiom.getIdiom());
            System.out.println("---------------------------------");
        }
    }

    private static void repeat(File in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("请输入上一个结尾读音：");
        String startPronunciation = reader.readLine();

        List<Idiom> idiomList = IdiomUtils.getRepeatablePath(in, startPronunciation);
        if (idiomList == null) {
            System.out.println("未找到结果");
        } else {

            System.out.println("结果如下\n\n---------------------------------");
            for (Idiom idiom : idiomList) {
                System.out.println(idiom.getIdiom());
            }
            System.out.println("---------------------------------");
        }
    }

}
