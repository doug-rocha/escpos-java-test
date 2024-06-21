package application;

import escpos.print.Commands;
import escpos.print.POSPrinter;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

/**
 *
 * @author Douglas Rocha de Oliveira
 */
public class Program {

    private static final String printerName = "Two Pilots Demo Printer";
    private static final String imagePath = "d:\\Users\\douglas\\Pictures\\DHs.png";

    private static PrintService mPrinter = null;
    private static Boolean bFoundPrinter = false;

    private static POSPrinter printerDriver = new POSPrinter();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, PrintException {

        Scanner sc = new Scanner(System.in);

        PrintService[] printServices = PrinterJob.lookupPrintServices();

        for (PrintService printService : printServices) {
            if (printerName.equals(printService.getName())) {
                mPrinter = printService;
                bFoundPrinter = true;
            }
        }
        //System.out.print("Insira texto para cabecalho: ");
        String cabecalho = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz\n0123456789 !@#$%^&*()-_=+\n";//sc.nextLine() + "\n";
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.INPUT_STREAM.AUTOSENSE;

        List<byte[]> list = new ArrayList<>();

        BufferedImage img = ImageIO.read(new File(imagePath));

        list.add(printerDriver.textoCondensado(cabecalho));
        list.add(printerDriver.image(img));
        list.add(Commands.PAPER_FULL_CUT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Commands.CHARCODE_PC860);
        baos.write(Commands.TXT_ALIGN_CT);
        baos.write(Commands.TXT_UNDERL_ON);
        baos.write(Commands.TXT_BOLD_ON);
        baos.write(cabecalho.getBytes());
        baos.write(Commands.TXT_UNDERL_OFF);
        baos.write(Commands.CTL_LF);
        baos.write(printerDriver.image(img));
        //baos.write(printerDriver.cortePapel());
        baos.write(Commands.CTL_LF);
        baos.write(Commands.TXT_NORMAL);
        baos.write(Commands.TXT_ALIGN_LT);
        baos.write(cabecalho.getBytes());
        baos.write(Commands.CTL_LF);
        baos.write(printerDriver.alturaCodBarras(32));
        baos.write(printerDriver.codEAN13("7892840818685"));
        baos.write(Commands.TXT_ALIGN_RT);
        baos.write(printerDriver.qrcode(15, "https://github.com/doug-rocha"));

        print(new ByteArrayInputStream(baos.toByteArray()), flavor);
    }

    private static void print(List<byte[]> commands, DocFlavor docFlavor) throws PrintException {
        if (!bFoundPrinter) {
            throw new IllegalStateException("No printer found");
        }
        DocPrintJob job = mPrinter.createPrintJob();
        Doc doc;
        for (byte[] command : commands) {
            doc = new SimpleDoc(command, docFlavor, null);
            job.print(doc, null);
        }
    }

    private static void print(ByteArrayInputStream commands, DocFlavor docFlavor) throws PrintException {
        if (!bFoundPrinter) {
            throw new IllegalStateException("No printer found");
        }
        DocPrintJob job = mPrinter.createPrintJob();
        Doc doc = new SimpleDoc(commands, docFlavor, null);
        job.print(doc, null);
    }

}
