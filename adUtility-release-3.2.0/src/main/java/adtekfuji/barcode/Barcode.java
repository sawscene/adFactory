/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.barcode;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 *
 * @author nar-nakamura
 */
public class Barcode {

    private Barcode() {
    }

    /**
     * QRコード画像を作成する。
     *
     * @param contents
     * @param level
     * @param charset
     * @param size
     * @return
     * @throws com.google.zxing.WriterException
     */
    public static BufferedImage createQRCodeImage(String contents, ErrorCorrectionLevel level, String charset, int size) throws WriterException  {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, level);
        hints.put(EncodeHintType.CHARACTER_SET, charset);
        hints.put(EncodeHintType.MARGIN, 4);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

        return image;
    }
}
