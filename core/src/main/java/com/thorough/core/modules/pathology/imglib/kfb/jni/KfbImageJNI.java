package com.thorough.core.modules.pathology.imglib.kfb.jni;


import com.thorough.library.utils.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class KfbImageJNI {

    /*
    *
    * 这个类的路径不能随便改，一旦改了以后，libkfbImage-jni.so文件需要重新生成
    *
    * */
    protected  Logger logger = LoggerFactory.getLogger(getClass());
    private KfbImageJNI() {
    }

    static {
        try {
            System.load(PropertyUtil.getProperty("pathology.image.kfb.jni-so"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    static {
//        try {
//            ClassPathResource classPathResource = new ClassPathResource("c-lib/libkfbImage-jni.so");
//            InputStream inputStream = classPathResource.getInputStream();
//            File f = File.createTempFile("JNI-", "Temp");
//            FileOutputStream out = new FileOutputStream(f);
//            byte [] buf = new byte[1024];
//            int len;
//            while ((len = inputStream.read(buf)) > 0)
//                out.write(buf, 0, len);
//            inputStream.close();
//            out.close();
//            System.load(f.getAbsolutePath());
//            f.delete();
//        } catch (Exception e) {
//            LoggerFactory.getLogger(KfbImageJNI.class).error("kfbImageJni error: ",e);
//            e.printStackTrace();
//        }
//    }

    public native static long openImageFile(String file);

    public native static void closeImageFile(long osr);

    public native static byte[] getImageStreamData(String file, float fScale, int x, int y);

    public native static byte[] getImageStreamDataRoi(String file, float fScale, int x, int y,int width,int height);

    public native static void getImageRGBStreamData(long osr, float fscale, int x, int y, int w, int h);

    public native static byte[] getLabelInfo(String file);

    public static String[] getHeaderInfo(String imageFile) throws Exception{
        String command = PropertyUtil.getProperty("pathology.image.kfb.shell") + " " + imageFile;
        Process pro = Runtime.getRuntime().exec(command);
        InputStream stream = pro.getInputStream();
        String message = printMessage(stream);
        if(message.length() == 0)
            throw new Exception("没有获取到kfb的头信息");
        else if(message.split(" ").length !=4)
            throw new Exception("kfb的头信息不完整，缺少长、宽、块大小、比例中的一个或者多个信息");
        else  return message.split(" ");
    }

    private static String printMessage(InputStream input) {
        Reader reader = new InputStreamReader(input);
        BufferedReader bf = new BufferedReader(reader);
        String line = "";
        String message = "";
        try {
            while((line=bf.readLine())!=null) {
                message += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;

    }

}
