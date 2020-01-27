package com.qzero.repeat;

import java.io.File;
import java.io.RandomAccessFile;

public class IdiomSeeker {

    private int currentLine=1;
    private File in;

    public IdiomSeeker(File in) {
        this.in = in;
    }

    private int getStartLineOffset(){
        return (currentLine-1)*30;
    }

    public void nextLine(){
        currentLine++;
    }

    public Idiom getIdiom(){
        int startOffset=getStartLineOffset();

        try {
            RandomAccessFile randomAccessFile=new RandomAccessFile(in,"r");
            randomAccessFile.seek(startOffset);
            byte buf[]=new byte[30];
            randomAccessFile.read(buf);

            String line=new String(buf);
            line=line.replaceAll("( |\n)","");
            String[] parts=line.split(",");

            if(parts.length!=3){
                return null;
            }

            return new Idiom(parts[0],parts[1],parts[2]);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 定位到该读音的首行
     * @param startPronunciation
     * @return
     */
    public boolean locateToPronunciation(String startPronunciation){
        try {
            currentLine=1;

            while (true){
                Idiom idiom=getIdiom();

                if(idiom==null)
                    return false;

                if(idiom.getStartPronunciation().equals(startPronunciation)){
                    return true;
                }
                currentLine++;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
