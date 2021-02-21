package io.github.my_byte_2021.cioppino;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;

public class TocSupport extends PdfPageEventHelper {
    private final File[] files;
    private final PdfOutline root;
    private String prevDir;
    private PdfOutline prevDirOutline;

    public TocSupport(File[] files, PdfOutline root) {
        this.files = files;
        this.root = root;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        int pageNumber = document.getPageNumber();
        File file = files[pageNumber - 1];
        String dir = file.getParentFile().getName();
        if (!dir.equals(prevDir)) {
            prevDir = dir;
            prevDirOutline = pageOutline(pageNumber, dir, root, writer);
        }
        pageOutline(pageNumber, file.getName(), prevDirOutline, writer);
    }

    private static PdfOutline pageOutline(int pageNumber, String title, PdfOutline parent, PdfWriter writer) {
        PdfDestination pd = new PdfDestination(PdfDestination.FITH, 0);
        PdfAction pdfAction = PdfAction.gotoLocalPage(pageNumber, pd, writer);
        return new PdfOutline(parent, pdfAction, title);
    }
}
