/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

/**
 *
 * @author n-ando
 */
public class StringOperation {

    private StringOperation() {
    }

    public static byte changeXOR(byte[] data) {
        byte ret = 0;
        for (byte b : data) {
            ret ^= b;
        }
        return ret;
    }

}
