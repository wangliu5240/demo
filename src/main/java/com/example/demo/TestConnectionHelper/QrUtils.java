package com.example.demo.TestConnectionHelper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QrUtils {

    public static byte[] toByteArray(String context) {
        byte[] res = new byte[0];
        //设置生个图片格式
        String format = "png";

        //设置额外参数
        Map<EncodeHintType, Object> map = new HashMap<>();
        //设置编码集
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错率，指定容错等级，例如二维码中使用的ErrorCorrectionLevel
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //生成条码的时候使用，指定边距，单位像素，受格式的影响。类型Integer, 或String代表的数字类型
        map.put(EncodeHintType.MARGIN, 2);
        try {
            //生成二维码，（参数为：编码的内容、编码的方式（二维码、条形码...）、首选的宽度、首选的高度、编码时的额外参数）
            BitMatrix encode = new MultiFormatWriter().encode(context, BarcodeFormat.QR_CODE, 200, 200, map);

            //生成二维码图片，并将二维码写到文件里
            //MatrixToImageWriter.writeToPath(encode,format,new File("D:/test.png").toPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(encode, format, stream);
            res = stream.toByteArray();
            stream.close();

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
