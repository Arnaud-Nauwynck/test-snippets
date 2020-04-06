package fr.an.tools.flipbook2pdf.impl;

import java.io.File;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DownloadBookCtx {
    
    public File bookOutputDir;
    public final String bookTitle;
    public final String orderDetail;
    public BookConfig bookConfig;

}
