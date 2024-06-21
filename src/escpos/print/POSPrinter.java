package escpos.print;

import escpos.print.image.PrinterImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class POSPrinter {

    public byte[] codEAN13(String cod) {
        byte[] b = new byte[3 + cod.length() + 1];
        b[0] = 0x1d;
        b[1] = 0x6b;
        b[2] = 0x02;
        System.arraycopy(cod.getBytes(), 0, b, 3, cod.length());
        b[cod.length() + 3] = 0x00;
        return b;
    }

    public byte[] codEAN8(String cod) {
        byte[] b = new byte[3 + cod.length() + 1];
        b[0] = 0x1d;
        b[1] = 0x6b;
        b[2] = 0x03;
        System.arraycopy(cod.getBytes(), 0, b, 3, cod.length());
        b[cod.length() + 3] = 0x00;
        return b;
    }

    public byte[] codUPCA(String cod) {
        byte[] b = new byte[3 + cod.length() + 1];
        b[0] = 0x1d;
        b[1] = 0x6b;
        b[2] = 0x00;
        System.arraycopy(cod.getBytes(), 0, b, 3, cod.length());
        b[cod.length() + 3] = 0x00;
        return b;
    }

    public byte[] cod128(String cod) {
        byte[] b = new byte[3 + 2 + cod.length()];
        b[0] = 0x1d;
        b[1] = 0x6b;
        b[2] = 0x49;
        b[3] = (byte) (cod.length() + 2);
        System.arraycopy(cod.getBytes(), 0, b, 4, cod.length());
        return b;
    }

    public byte[] alturaCodBarras(int altura) {
        byte[] b = new byte[3];
        b[0] = 0x1d;
        b[1] = 0x68;
        b[2] = (byte) altura;
        return b;
    }

    public byte[] textoNegrito(String texto) {
        byte[] b = new byte[6 + texto.length() + 3];
        b[0] = 0x1b;
        b[1] = 0x21;
        b[2] = 0x00;
        b[3] = 0x1b;
        b[4] = 0x47;
        b[5] = (byte) 0xff;
        System.arraycopy(texto.getBytes(), 0, b, 6, texto.length());
        b[texto.length() + 5 + 1] = 0x1b;
        b[texto.length() + 5 + 2] = 0x47;
        b[texto.length() + 5 + 3] = 0x00;
        return b;
    }

    public byte[] textoCondensado(String texto) {
        byte[] b = new byte[6 + texto.length() + 3];
        b[0] = 0x1b;
        b[1] = 0x21;
        b[2] = 0x00;
        b[3] = 0x1b;
        b[4] = 0x4d;
        b[5] = 0x01;
        System.arraycopy(texto.getBytes(), 0, b, 6, texto.length());
        b[texto.length() + 5 + 1] = 0x1b;
        b[texto.length() + 5 + 2] = 0x4d;
        b[texto.length() + 5 + 3] = 0x00;
        return b;
    }

    public byte[] cortePapel() {
        return new byte[]{0x1b, 0x6d};
    }

    public byte[] abreGaveta(int pino, int pulso) {
        byte[] b = new byte[5];
        b[0] = 0x10;
        b[1] = 0x14;
        b[2] = 0x01;
        b[3] = (byte) pino;
        b[4] = (byte) pulso;
        return b;
    }

    public byte[] qrcode(int size, String data) {
        int l = data.length() + 3;
        byte[] b = new byte[9 + 8 + 8 + data.length() + 8];
        //function 65 - mode
        System.arraycopy(new byte[]{0x1d, 0x28, 0x6b, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00}, 0, b, 0, 9);
        //function 67 - size
        System.arraycopy(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x43}, 0, b, 9, 7);
        b[9 + 7] = (byte) size;
        //function 80 - save data        
        b[9 + 8 + 0] = 0x1d;
        b[9 + 8 + 1] = 0x28;
        b[9 + 8 + 2] = 0x6b;
        b[9 + 8 + 3] = (byte) (l % 256);
        b[9 + 8 + 4] = (byte) (l / 256);
        b[9 + 8 + 5] = 0x31;
        b[9 + 8 + 6] = 0x50;
        b[9 + 8 + 7] = 0x30;
        System.arraycopy(data.getBytes(), 0, b, 9 + 8 + 8, data.length());
        //function 81 - print data
        System.arraycopy(new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x51, 0x30}, 0, b, 9 + 8 + 8 + data.length(), 8);
        return b;
    }

    public byte[] image(BufferedImage bImage) throws IOException {
        PrinterImage img = new PrinterImage();
        int[][] pixels = img.getPixelsSlow(bImage);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int y = 0; y < pixels.length; y += 24) {
            out.write(Commands.LINE_SPACE_24);
            out.write(Commands.SELECT_BIT_IMAGE_MODE);
            out.write(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
            for (int x = 0; x < pixels[y].length; x++) {
                out.write(img.recollectSlice(y, x, pixels));
            }
            out.write(Commands.CTL_LF);
        }
        return out.toByteArray();
    }

    public byte[] pedeStatusDrawer() {
        return new byte[]{0x10, 0x04, 0x01};
    }

    public byte[] pedeStatusBotaoFeed() {
        return new byte[]{0x10, 0x04, 0x02};
    }

    public byte[] pedeStatusPapelTampa() {
        return new byte[]{0x10, 0x04, 0x04};
    }

    public byte[] pedeStatusNivelPapel() {
        return new byte[]{0x1d, 0x72, 0x01};
    }

    public boolean drawerOn(byte status) {
        return (status & 0b10111) != 0b10111;
    }

    public boolean botaoFeedPressed(byte status) {
        return (status & 0b11111) == 0b11010;
    }

    public boolean papelTampaOK(byte status) {
        return (status & 0b1110011) != 0b1110010;
    }

    public boolean nivelPapeOK(byte status) {
        return (status & 0b11100) != 0b01100;
    }

}
