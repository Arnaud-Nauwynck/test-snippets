package fr.an.tools.flipbook2pdf.impl;

import lombok.Builder;
import lombok.Getter;

/** info loaded from book config.js */
@Builder // @AllArgsConstructor
@Getter
public class BookConfig {
    long createdTime;
    int totalPageCount;
    int largePageWidth;
    int largePageHeight;
}