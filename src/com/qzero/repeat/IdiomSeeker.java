package com.qzero.repeat;

import java.io.File;
import java.io.RandomAccessFile;

public class IdiomSeeker {

    private int currentLine=1;
    private File in;
    private int totalCount;

    public IdiomSeeker(File in) {
        this.in = in;
        totalCount= (int) (in.length()/30);
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
        currentLine=locateToPronunciationBinarySearch(startPronunciation,0,1,totalCount);
        if(currentLine<=0){
            currentLine=1;
            return false;
        }
        return true;
    }

    private byte[] getCurrentIdiomBuf(RandomAccessFile randomAccessFile) throws Exception{
        int startOffset=getStartLineOffset();
        randomAccessFile.seek(startOffset);
        byte buf[]=new byte[30];
        randomAccessFile.read(buf);
        return buf;
    }

    private int locatePronunciationBoundLeft(byte letter,int currentIndex,int left,int right) throws Exception{
        RandomAccessFile randomAccessFile=new RandomAccessFile(in,"r");

        while (left<right){
            int mid=(left+right)/2;
            currentLine=mid;

            byte[] midBuf=getCurrentIdiomBuf(randomAccessFile);
            if(midBuf==null || midBuf.length==0)
                return -1;

            if(midBuf[currentIndex]==letter){
                right=mid;
            }else if(midBuf[currentIndex]<letter){
                left=mid+1;
            }else if(midBuf[currentIndex]>letter){
                right=mid;
            }
        }

        return left;
    }

    private int locatePronunciationBoundRight(byte letter,int currentIndex,int left,int right) throws Exception{
        RandomAccessFile randomAccessFile=new RandomAccessFile(in,"r");

        while (left<right){
            int mid=(left+right)/2;
            currentLine=mid;

            byte[] midBuf=getCurrentIdiomBuf(randomAccessFile);
            if(midBuf==null || midBuf.length==0)
                return -1;

            if(midBuf[currentIndex]==letter){
                left=mid+1;
            }else if(midBuf[currentIndex]<letter){
                left=mid+1;
            }else if(midBuf[currentIndex]>letter){
                right=mid;
            }
        }

        return right-1;
    }

    /**
     * 二分递归寻找读音首行
     * 先定位第一个字母的开始与结束，再定位第二个字母的开始与结束，以此类推，直到定位到分隔符，返回0，那么上一个即为开始行
     */
    private int locateToPronunciationBinarySearch(String startPronunciation,int currentIndex,int left,int right){
        try {
            byte[] p=startPronunciation.getBytes();
            if(p.length==currentIndex+1){
                return locatePronunciationBoundLeft(p[currentIndex],currentIndex,left,right);
            }
            byte currentChar= p[currentIndex];

            left=locatePronunciationBoundLeft(currentChar,currentIndex,left,right);
            right=locatePronunciationBoundRight(currentChar,currentIndex,left,right);

            //System.out.println("Found: index:"+currentIndex+"("+(char)currentChar+"),position in file:["+left+","+right+"]");
            return locateToPronunciationBinarySearch(startPronunciation,currentIndex+1,left,right);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

}
