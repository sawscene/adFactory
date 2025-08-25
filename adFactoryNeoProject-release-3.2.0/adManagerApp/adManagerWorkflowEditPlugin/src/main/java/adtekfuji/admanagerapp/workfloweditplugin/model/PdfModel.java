/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * PDFモデル
 *
 * @author s-heya
 */
public class PdfModel {
    private PDDocument document;
    private PDFRenderer renderer;

    /**
     * コンストラクタ
     *
     * @param path
     */
    public PdfModel(Path path) {
        try {
            this.document = Loader.loadPDF(path.toFile());
            this.renderer = new PDFRenderer(document);
        } catch (IOException ex) {
            throw new UncheckedIOException("PDDocument thorws IOException file=" + path, ex);
        }
    }

    /**
     * ページ数を取得する。
     *
     * @return
     */
    public int numPages() {
        return document.getPages().getCount();
    }

    /**
     * PDFからImageを取得する。
     *
     * @param pageNumber
     * @return
     */
    public Image getImage(int pageNumber) {
        BufferedImage pageImage;
        try {
            pageImage = this.renderer.renderImage(pageNumber);
        } catch (IOException ex) {
            throw new UncheckedIOException("PDFRenderer throws IOException", ex);
        }
        return SwingFXUtils.toFXImage(pageImage, null);
    }

    /**
     * PDFを閉じる
     */
    public void close() {
        try {
            if (Objects.nonNull(this.document)) {
                this.document.close();
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException("PDDocument throws IOException", ex);
        }
    }
}
