/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.platform.windows;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.Objects;
import jp.adtekfuji.platform.enumerate.InputScope;

/**
 * プラットフォームサービス
 *
 * @author s-heya
 */
public class PlatformService {

    /**
     * Msctf.dll
     */
    interface Msctf extends Library {
        Msctf INSTANCE = Native.loadLibrary("msctf.dll", Msctf.class);
        WinNT.HRESULT SetInputScope(WinDef.HWND hwnd, int inputScope);
    }

    /**
     * imm32.dll
     */
    interface Imm32 extends Library {
        Imm32 INSTANCE = Native.loadLibrary("imm32.dll", Imm32.class);
        WinNT.DWORD ImmGetContext(WinDef.HWND hwnd);
        boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.DWORD himc);
        boolean ImmGetOpenStatus(WinNT.DWORD himc);
        boolean ImmGetConversionStatus(WinNT.DWORD himc, IntByReference conversion, IntByReference sentence);
    }

    /**
     * IMEを操作して、入力文字種を変更する。
     *
     * @param inputScope
     */
    public static void SetInputScope(InputScope inputScope) {
        int value = 0;
        switch (inputScope) {
            case NUMBER:
                value = 29;
                break;
            case ALPHANUMERIC_HALF:
                value = 40;
                break;
            case ALPHANUMERIC_FULL:
                value = 41;
                break;
            case HIRAGANA:
                value = 44;
                break;
            default:
                value = 0;
                break;
        }

        WinDef.HWND hwnd = User32.INSTANCE.GetActiveWindow();
        if (Objects.nonNull(hwnd)) {
            Msctf.INSTANCE.SetInputScope(hwnd, value);
        }
    }

    /**
     * IMEの入力モードを取得する。
     *
     * @return
     */
    public static int GetInputScope() {
        IntByReference conversion = new IntByReference();

        WinDef.HWND hwnd = User32.INSTANCE.GetActiveWindow();
        if (Objects.nonNull(hwnd)) {
            WinNT.DWORD himc = Imm32.INSTANCE.ImmGetContext(hwnd);
            if (!Imm32.INSTANCE.ImmGetOpenStatus(himc)) {
                return conversion.getValue();
            }

            IntByReference sentence = new IntByReference();
            Imm32.INSTANCE.ImmGetConversionStatus(himc, conversion, sentence);
            Imm32.INSTANCE.ImmReleaseContext(hwnd, himc);
        }

        return conversion.getValue();
    }
}
