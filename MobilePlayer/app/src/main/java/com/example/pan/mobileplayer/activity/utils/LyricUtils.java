package com.example.pan.mobileplayer.activity.utils;

import com.example.pan.mobileplayer.activity.domain.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pan on 2018/10/6.
 * 解析歌词
 */
public class LyricUtils {

    /**
     * 得到解析好的歌词列表
     * @return
     */
    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }
    private ArrayList<Lyric> lyrics;


    public boolean isExistsLyric() {
        return isExistsLyric;
    }

    /**
     * 是否存在歌词
     */
    private boolean isExistsLyric  = false;


    public void readLyric(File file) {
        if (file == null || !file.exists()) {
            //歌词不存在或为空
            lyrics = null;
            isExistsLyric = false;
        } else {
            //解析歌词--一行读取
            lyrics = new ArrayList<>();
            isExistsLyric = true;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), getCharset(file)));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    line = analysisLyric(line);
                }
                reader.close();//关闭


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //2.排序
            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric lhs, Lyric rhs) {
                    if(lhs.getTimePoint() < rhs.getTimePoint()){
                        return  -1;
                    }else if(lhs.getTimePoint() > rhs.getTimePoint()){
                        return  1;
                    }else{
                        return 0;
                    }

                }
            });

            //3.计算每句高亮显示的时间
            for(int i=0;i<lyrics.size();i++){
                Lyric oneLyric = lyrics.get(i);
                if(i+1 < lyrics.size()){
                    Lyric twoLyric = lyrics.get(i+1);
                    oneLyric.setSleepTime(twoLyric.getTimePoint()-oneLyric.getTimePoint());
                }
            }

        }
    }

    /**
     * 判断文件编码
     * @param file 文件
     * @return 编码：GBK,UTF-8,UTF-16LE
     */
    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    /**
     * 解析一句歌词
     *
     * @return
     */
    private String analysisLyric(String line) {
        //indexOf方法第一次出现"["的位置
        int site1 = line.indexOf("[");//0,如果没有返回-1
        int site2 = line.indexOf("]");//9,如果没有返回-1
        if (site1 == 0 && site2 != -1) {

            //装时间数组
            long[] times = new long[getTotal(line)];
            String strTime = line.substring(site1 + 1, site2);
            times[0] = conversionTime(strTime);

            String content = line;
            int i = 1;
            while (site1 == 0 && site2 != -1) {
                content = content.substring(site2 + 1); //[03:37.32][00:59.73]
                site1 = content.indexOf("[");//0/-1
                site2 = content.indexOf("]");//9//-1

                if (site2 != -1) {
                    strTime = content.substring(site1 + 1, site2);//03:37.32-->00:59.73
                    times[i] = conversionTime(strTime);

                    if (times[i] == -1) {
                        return "";
                    }
                    i++;
                }

            }

            Lyric lyric = new Lyric();
            //把时间数组和文本关联起来，并且加入到集合中
            for (int j = 0; j < times.length; j++) {

                if (times[j] != 0) {//有时间戳

                    lyric.setContent(content);
                    lyric.setTimePoint(times[j]);
                    //添加到集合中
                    lyrics.add(lyric);
                    lyric = new Lyric();

                }
            }
            return content;//返回歌词内容
        }

        return "";
    }

    /**
     * 时间的String类型转换long类型
     * 歌词时间[02:04.12]
     * @param strTime
     * @return
     */
    private long conversionTime(String strTime) {
        long result = -1;
        try {
            //1.把[02:04.12]按照:切割成02和04.12
            String[] s1 = strTime.split(":");
            //2.把04.12按照.切割成04和12
            String[] s2 = s1[1].split("\\.");

            //1.分
            long min = Long.parseLong(s1[0]);

            //2.秒
            long second = Long.parseLong(s2[0]);

            //3.毫秒
            long milliscond = Long.parseLong(s2[1]);

            //得到的最后的long类型时间
            result = min * 60 * 1000 + second * 1000 + milliscond * 10;
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }

    /**
     * 总计多少歌词--判断
     *
     * @param line
     * @return
     */
    private int getTotal(String line) {
        int result = -1;
        String[] left = line.split("\\[");
        String[] rigth = line.split("\\]");
        if (left.length == 0 && rigth.length == 0) {
            result = 1;
        } else if (left.length > rigth.length) {
            result = left.length;
        } else {
            result = rigth.length;
        }
        return result;
    }
}
