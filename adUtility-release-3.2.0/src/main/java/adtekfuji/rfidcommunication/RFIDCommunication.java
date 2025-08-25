/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.rfidcommunication;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 *
 * @author Bui Thanh Tung
 */
public class RFIDCommunication {

    private RFIDCommunicationListener controllerListener = null;

    public RFIDCommunication() {

    }

    public void readData() throws CardException {
        Thread readThread = new Thread(new Reader(controllerListener));
        readThread.start();
    }

    public void setListener(RFIDCommunicationListener controller) {
        this.controllerListener = controller;
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    public static class Reader implements Runnable {

        RFIDCommunicationListener controlListener;
        CardTerminal terminal = null;

        public Reader(RFIDCommunicationListener controlListener) throws CardException {
            this.controlListener = controlListener;
            TerminalFactory factory = TerminalFactory.getDefault();
            if (factory.terminals() != null && factory.terminals().list() != null) {
                List<CardTerminal> terminals = factory.terminals().list();
                for (CardTerminal t : terminals) {
                    if (t.getName().contains("ACR122")) {
                        terminal = t;
                        break;
                    }
                }
            }
        }

        @Override
        public void run() {
            if (terminal != null) {
                Card card;

                try {
                    if (terminal.waitForCardPresent(60000)) {
                        card = terminal.connect("*");
                        CardChannel channel = card.getBasicChannel();
                        byte[] c1 = {(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00}; //UID取得
                        ResponseAPDU r = channel.transmit(new CommandAPDU(c1));
                        byte[] result = new byte[4];
                        for (int i = 0; i < 4; i++) {
                            result[i] = r.getBytes()[i];
                        }
                        String msg = byteArrayToHexString(result);
                        this.controlListener.listener(msg);
                        card.disconnect(false);
                    } else {
                        this.controlListener.listener("");
                    }

                } catch (CardException ex) {
                    Logger.getLogger(RFIDCommunication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
