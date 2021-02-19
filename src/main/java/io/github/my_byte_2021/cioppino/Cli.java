package io.github.my_byte_2021.cioppino;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.lang.String.format;

public class Cli implements Callable<Integer> {
    @Option(names = "-S", description = "skip sorting FILEs")
    private boolean skipSort;

    @Option(names = {"--overwrite", "-W"}, description = "overwrite output file")
    private boolean overwrite;

    @Option(names = "--debug", hidden = true)
    private boolean debug;

    @Parameters(index = "0", paramLabel = "OUTPUT", description = "path to output file")
    private File output;

    @Parameters(index = "1..*", arity = "1..*", paramLabel = "FILE", description = "input files")
    private File[] files;

    @Override
    public Integer call() throws Exception {
        try {
            mergeImages();
        } catch (Exception e) {
            log("Error: " + e.getMessage());
            if (debug) {
                e.printStackTrace(System.err);
            }
            return 1;
        }
        return 0;
    }

    private void mergeImages() throws IOException {
        if (output.exists() && !overwrite) {
            throw new RuntimeException(format("output file %s already exists", output));
        }

        log("Generating %s...", output);
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);
        PdfWriter.getInstance(document, new FileOutputStream(output));
        document.open();

        if (!skipSort) {
            Arrays.sort(files);
        }

        for (File file : files) {
            String filename = file.toString();
            log("Adding %s...", filename);

            Image image = Image.getInstance(filename);
            document.setPageSize(new Rectangle(image.getWidth(), image.getHeight()));
            document.newPage();
            document.add(image);
        }

        document.close();
        log("Done.");
    }

    public static void main(String[] args) {
        int status = new CommandLine(new Cli()).execute(args);
        System.exit(status);
    }

    private static void log(String format, Object... args) {
        System.err.printf(format, args);
        System.err.println();
    }
}
