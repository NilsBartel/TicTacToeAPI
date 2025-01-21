package tictactoe.api;

import org.xnio.IoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

 import org.apache.commons.io.IOUtils;


public class Handler {


    public static List<String> splitPath(String path) {
        String[] parts = path.split("/");
        return new ArrayList<>(Arrays.asList(parts));
    }

//    public static String buildStringTest(InputStream inputStream) throws IOException {
////        inputStream.toString(inputStream, 0);
////        String result = IOUtils.toString(inputStream, null);
////        IOU
//
//        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//        return result;
//    }

//    public static String buildString(InputStream inputStream) throws IOException {
//        InputStreamReader isr =  new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//        BufferedReader br = new BufferedReader(isr);
//        int b;
//        StringBuilder buf = new StringBuilder(512);
//        while ((b = br.read()) != -1) {
//            buf.append((char) b);
//        }
//        br.close();
//        isr.close();
//
//        return buf.toString();
//    }






}
