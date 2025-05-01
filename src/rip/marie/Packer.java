/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package rip.marie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import rip.marie.packingShit.BinaryFile;

public class Packer {
    public static ArrayList<BinaryFile> files;

    public static String pack(File inputFile) {
        files = new ArrayList();
        byte[] bytes = Packer.readInputStream(Packer.class.getResourceAsStream("/cock.bin"));
        if (bytes == null) {
            JOptionPane.showMessageDialog(null, "Unable to read base file, for more information contact support!");
            return null;
        }
        File tempFile = Packer.getTempFile();
        if (tempFile == null) {
            JOptionPane.showMessageDialog(null, "Unable to create temp file, for more information contact support!");
            return null;
        }
        tempFile.deleteOnExit();
        for (int i = 0; i < bytes.length; ++i) {
            int n = i;
            bytes[n] = (byte)(bytes[n] ^ (byte)(0x1A4 ^ i * 3));
        }
        Packer.writeFile(tempFile, bytes);
        Packer.doZipShit(tempFile);
        Packer.kollaps(inputFile);
        String path = inputFile.getAbsoluteFile() + "-packed.jar";
        Packer.kys(path);
        return path;
    }

    private static void kys(String path) {
        try (OutputStream fileStream = Files.newOutputStream(new File(path).toPath(), new OpenOption[0]);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileStream);){
            for (BinaryFile ncf : files) {
                if (ncf.getName().endsWith("/")) continue;
                zipOutputStream.putNextEntry(new ZipEntry(ncf.getName()));
                zipOutputStream.write(ncf.getBytes());
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to write output file, for more information contact support!");
            throw new RuntimeException(e);
        }
    }

    private static void kollaps(File inputFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            byte[] b = Packer.readInputStream(fileInputStream);
            if (b == null) {
                throw new Exception("Shit is null");
            }
            for (int i = 0; i < b.length; ++i) {
                int n = i;
                b[n] = (byte)(b[n] ^ (byte)(69 + i));
            }
            files.add(new BinaryFile("kurwa.bin", b));
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null, "Failed to read input file, for more information contact support!");
            throw new RuntimeException(t);
        }
    }

    private static void doZipShit(File tempFile) {
        try (InputStream inputFileStream = Files.newInputStream(tempFile.toPath(), new OpenOption[0]);
             ZipInputStream zipStream = new ZipInputStream(inputFileStream);){
            ZipEntry innerFile;
            while ((innerFile = zipStream.getNextEntry()) != null) {
                files.add(new BinaryFile(innerFile.getName(), Packer.readInputStream(zipStream)));
                zipStream.closeEntry();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed some zip stuff idk");
            throw new RuntimeException(e);
        }
    }

    private static void writeFile(File tempFile, byte[] bytes) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile);){
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to write temp file, for more information contact support!");
        }
    }

    private static File getTempFile() {
        try {
            return File.createTempFile("mizu_sex_", ".jar");
        } catch (Throwable t) {
            return null;
        }
    }

    public static byte[] readInputStream(InputStream inputStream2) {
        try {
            int bytesRead;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream2.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable t) {
            return null;
        }
    }
}

