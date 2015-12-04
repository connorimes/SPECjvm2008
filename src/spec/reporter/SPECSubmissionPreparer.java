package spec.reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import spec.harness.Util;
import spec.harness.results.TestResult;

public class SPECSubmissionPreparer {

    public static void prepareFiles(String [] args) throws IOException {

        int files = 0;
        
        for (int i = 0; i < args.length; i++) {
            if (!(new File(args[i]).exists())) {
                System.out.println("Cannot find file '" + args[0] + "'");
                return;
            } else {
                files++;
            }
        }

        File [] rawFiles = new File[files];
        for (int i = 0; i < files; i++) {
            rawFiles[i] = new File(args[i]);
        }
        
        String newRawFile = mergeFiles(rawFiles);

        if (newRawFile == null) {
            return;
        }
        
        zipFile(newRawFile);
        
        
    }
    
    private static String mergeFiles(File [] rawFiles) throws IOException {

        int files = rawFiles.length;

        BufferedReader [] rawFileStreams = new BufferedReader[files];
        for (int i = 0; i < files; i++) {
            rawFileStreams[i] = new BufferedReader(new InputStreamReader(new FileInputStream(rawFiles[i])));
        }

        String [] headerLines = new String[files];
        for (int i = 0; i < files; i++) {
            headerLines[i] = rawFileStreams[i].readLine();
        }
        
        for (int i = 0; i < files; i++) {
            String line = headerLines[i];
            if (line == null || line.indexOf("xml") == -1) {
                System.out.println("Error: '" + rawFiles[i].getName() + "' is not a raw file on correct format.");
                System.out.println("Expected header '" + TestResult.XML_HEADER + "'");
                return null;
            }
        }
        
        if (files > 1 && !headerLines[0].equals(headerLines[1])) {
            System.out.println("Error: '" +  rawFiles[0].getName() + "' and  '" +  rawFiles[1].getName() + "' does not have the same format.");
        }

        String resultFileName = Util.getNextRawFileInDir();
        System.out.println("Creating file " + resultFileName);
        File resultFile = new File(resultFileName);
        PrintStream resultFileStream = new PrintStream(new FileOutputStream(resultFile));
        
        resultFileStream.println(headerLines[0]);
        resultFileStream.println("<specjvm-results>");
        resultFileStream.println("  <spec.jvm2008.result.noncompliant>no</spec.jvm2008.result.noncompliant>");
        resultFileStream.println("  <spec.jvm2008.result.noncompliant.reason></spec.jvm2008.result.noncompliant.reason>");
        resultFileStream.println("  <spec.jvm2008.result.noncompliant.remedy></spec.jvm2008.result.noncompliant.remedy>");
        resultFileStream.println("  <spec.jvm2008.result.date>" + new Date().toString() + "</spec.jvm2008.result.date>");
        
        for (int i = 0; i < files; i++) {
            String line = rawFileStreams[i].readLine();
            while (line != null) {
                resultFileStream.println("  " + line);                
                line = rawFileStreams[i].readLine();
            }
        }

        resultFileStream.println("</specjvm-results>");
        resultFileStream.flush();
        resultFileStream.close();

        for (int i = 0; i < files; i++) {
            rawFileStreams[i].close();
        }
        
        return resultFileName;
    }
    
    private static void zipFile(String fileName) throws IOException {

        // Create a buffer for reading the files
        byte[] buf = new byte[2048];

        // Create the ZIP file
        String outFilename = fileName + ".zip";
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

        // Compress the file
        FileInputStream in = new FileInputStream(fileName);

        // Add ZIP entry to output stream.
        out.putNextEntry(new ZipEntry(fileName));

        System.out.println("Creating zipped rawfile " + outFilename);
        
        // Transfer bytes from the file to the ZIP file
        for (int n = in.read(buf); n > 0; n = in.read(buf)) {
            out.write(buf, 0, n);
        }

        // Complete the entry
        out.closeEntry();
        in.close();

        // Complete the ZIP file
        out.close();
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: spec.reporter.Merger <raw file 1> <raw file 2 (optional)>");
        }
        prepareFiles(args);
    }
}