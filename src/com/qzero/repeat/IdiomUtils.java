package com.qzero.repeat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class IdiomUtils {


    private static String processPronunciation(String origin){

        /**
         * ɑāáǎà
         * oōóǒò
         * eēéěè
         * iīíǐì
         * uūúǔù
         * üǖǘǚǜ
         * ê
         * 
         * ńň
         */

        origin=origin.replaceAll("\"","");
        origin=origin.replaceAll("(ɑ|ā|á|ǎ|à)","a");
        origin=origin.replaceAll("(o|ō|ó|ǒ|ò)","o");
        origin=origin.replaceAll("(e|ē|é|ě|è)","e");
        origin=origin.replaceAll("(i|ī|í|ǐ|ì)","i");
        origin=origin.replaceAll("(u|ū|ú|ǔ|ù)","u");
        origin=origin.replaceAll("(ü|ǖ|ǘ|ǚ|ǜ)","v");
        origin=origin.replaceAll("(ê)","e");
        origin=origin.replaceAll("(\uE7C7)","m");
        origin=origin.replaceAll("(ń|ň|\uE7C8)","n");
        return origin;
    }

    public static boolean translation(File in,File out){
        if(!in.exists())
            return false;

        StringBuffer result=new StringBuffer();
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(in),"GB2312"));
            String line;
            while ((line=reader.readLine())!=null){
                String parts[]=line.split(",");
                String idiom=parts[1];
                if(idiom.length()!=6)
                    continue;

                idiom=idiom.replaceAll("\"","");

                String pronunciation=parts[2];
                parts=pronunciation.split(" ");
                if(parts.length!=7)
                    continue;

                String firstPronunciation=parts[0];
                String lastPronunciation=parts[6];

                firstPronunciation=processPronunciation(firstPronunciation);
                lastPronunciation=processPronunciation(lastPronunciation);

                result.append(firstPronunciation);
                result.append(",");
                result.append(lastPronunciation);
                result.append(",");
                result.append(idiom);
                result.append("\n");

                System.out.println(firstPronunciation+","+lastPronunciation+","+idiom);
            }

            FileOutputStream outputStream=new FileOutputStream(out);
            outputStream.write(result.toString().getBytes());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 标准化每一行，使得每一行字节数都是30（成语恒定3*4=12字节，分隔符2字节，换行符1字节，给读音留15字节足矣），如果不足就在后面加空格
     * @param in
     * @return
     */
    public static boolean processTranslated(File in,File out){
        if(!in.exists())
            return false;

        StringBuffer result=new StringBuffer();
        try{
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(in)));
            String line;
            while ((line=reader.readLine())!=null){
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                outputStream.write(line.getBytes());
                if(outputStream.size()>29)
                    throw new IllegalArgumentException("遇到怪物了，有字节数大于29的一行\t"+line);

                while (outputStream.size()!=29){
                    outputStream.write(" ".getBytes());
                }

                line=new String(outputStream.toByteArray());
                result.append(line);
                result.append("\n");
                System.out.println(line);
            }

            FileOutputStream outputStream=new FileOutputStream(out);
            outputStream.write(result.toString().getBytes());

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isRepeatable(Idiom idiom){
        return idiom.getStartPronunciation().equals(idiom.getEndPronunciation());
    }

    /**
     * 寻找一条达到复读的路径，以List的方式顺序返回
     * @param in
     * @param startPronunciation
     * @return
     */
    public static List<Idiom> getRepeatablePath(File in,String startPronunciation){
        Stack<Idiom> idiomStack=getRepeatablePath(in,startPronunciation,1);
        if(idiomStack==null)
            return null;


        List<Idiom> result=new ArrayList<>();
        while (!idiomStack.isEmpty()){
            result.add(idiomStack.pop());
        }

        return result;
    }

    /**
     * 寻找一条达到复读的路径，以栈的方式逆序返回
     * 使用递归，当递归层数达到5层还未找到时直接放弃（返回null）
     * 每一层中，首先找，如果该层有就新建一个栈，成语压栈并返回
     * 如果该层没有，就再次定位到头部，遍历每个成语，并调用自身，如果自身返回的不是null，那么把当前成语压栈，返回
     * @param in
     * @param startPronunciation
     * @return
     */
    private static Stack<Idiom> getRepeatablePath(File in, String startPronunciation,int currentLayerCount){
        Idiom idiom=getRepeatableIdiom(in,startPronunciation);
        if(idiom==null){

            //System.out.println("第"+currentLayerCount+"层没找到，当前发音"+startPronunciation);

            if(currentLayerCount>=5)
                return null;
            //调用自身
            IdiomSeeker seeker=new IdiomSeeker(in);
            seeker.locateToPronunciation(startPronunciation);
            Idiom currentIdiom;
            while (true){
                currentIdiom=seeker.getIdiom();
                if(currentIdiom==null || !currentIdiom.getStartPronunciation().equals(startPronunciation))
                    break;

                Stack<Idiom> idiomStack=getRepeatablePath(in,currentIdiom.getEndPronunciation(),currentLayerCount+1);
                if(idiomStack!=null){
                    idiomStack.push(currentIdiom);
                    return idiomStack;
                }

                seeker.nextLine();
            }
        }else{
            Stack<Idiom> idiomStack=new Stack<>();
            idiomStack.push(idiom);
            return idiomStack;
        }

        return null;
    }

    public static Idiom getRepeatableIdiom(File in,String startPronunciation){
        IdiomSeeker seeker=new IdiomSeeker(in);
        if(!seeker.locateToPronunciation(startPronunciation))
            return null;

        Idiom idiom;
        while (true){
            idiom=seeker.getIdiom();
            if(idiom==null || !idiom.getStartPronunciation().equals(startPronunciation))
                break;

            if(isRepeatable(idiom))
                return idiom;

            seeker.nextLine();
        }

        return null;
    }

    public static Idiom getNextIdiom(File in,String startPronunciation){
        IdiomSeeker seeker=new IdiomSeeker(in);
        seeker.locateToPronunciation(startPronunciation);
        return seeker.getIdiom();
    }

}
