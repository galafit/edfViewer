package com.biorecorder.basechart.swing;

import com.biorecorder.basechart.graphics.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by galafit on 30/12/17.
 */
public class SwingCanvas implements BCanvas {
    private Graphics2D g2;
    private List<AffineTransform> affineTransforms = new ArrayList<AffineTransform>();
    private List<Shape> boundsList = new ArrayList<Shape>();

    public SwingCanvas(Graphics2D g2) {
        this.g2 = g2;
        affineTransforms.add(g2.getTransform());
        boundsList.add(g2.getClip());
    }

    @Override
    public void translate(int x, int y) {
        g2.translate(x, y);
    }

    @Override
    public void rotate(float degree) {
        g2.rotate(Math.toRadians(degree));
    }

    @Override
    public void rotate(float degree, int pivotX, int pivotY) {
        g2.rotate(Math.toRadians(degree), pivotX, pivotY);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        g2.setClip(x, y, width, height);
    }

    @Override
    public BRectangle getBounds() {
        Rectangle bounds = g2.getClipBounds();
        return new BRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void save() {
        affineTransforms.add(g2.getTransform());
        boundsList.add(g2.getClip());
    }

    @Override
    public void restore() {
        int lastTransform = affineTransforms.size() - 1;
        if(lastTransform >= 0) {
            g2.setTransform(affineTransforms.get(lastTransform));
            g2.setClip(boundsList.get(lastTransform));
            boundsList.remove(lastTransform);
            affineTransforms.remove(lastTransform);
        }
    }

    @Override
    public void setTextStyle(TextStyle textStyle) {
        g2.setFont(getFont(textStyle));
    }

    private Font getFont(TextStyle textStyle) {
        String fontName = textStyle.getFontName();
        if(fontName == TextStyle.DEFAULT) {
            fontName = new JLabel().getFont().getFontName();
        }
        int style = Font.PLAIN;
        if(textStyle.isBold()) {
            style += Font.BOLD;
        }
        if(textStyle.isItalic()) {
            style += Font.ITALIC;
        }
        return new Font(fontName, style, textStyle.getSize());
    }

    @Override
    public TextMetric getTextMetric(TextStyle textStyle) {
        return new TextMetric() {
            FontMetrics fm = g2.getFontMetrics(getFont(textStyle));
            @Override
            public int ascent() {
                return fm.getAscent();
            }

            @Override
            public int descent() {
                return fm.getDescent();
            }

            @Override
            public int height() {
                return fm.getHeight();
            }

            @Override
            public int stringWidth(String str) {
                return fm.stringWidth(str);
            }
        };
    }


    @Override
    public void setColor(BColor color) {
        g2.setColor(new Color(color.getRed(),  color.getGreen(), color.getBlue(), color.getAlpha()));
    }

    @Override
    public void setStroke(BStroke stroke) {
        Stroke awtStroke = new BasicStroke(stroke.getWidth());

        if(stroke.getStyle() == BStroke.DASH_LONG) {
            float[] dash = {4f, 0f, 2f};
            awtStroke = new BasicStroke(stroke.getWidth(), BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
        }
        if(stroke.getStyle() == BStroke.DASH_SHORT) {
            float[] dash = {2f, 0f, 2f};
            awtStroke = new BasicStroke(stroke.getWidth(), BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
        }
        if(stroke.getStyle() == BStroke.DASH_DOT) {
            float[] dash = {4f, 4f, 1f};
            awtStroke = new BasicStroke(stroke.getWidth(), BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
        }
        if(stroke.getStyle() == BStroke.DOT) {
            float[] dash = {1f, 3f};
            awtStroke = new BasicStroke(stroke.getWidth(), BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 1.0f, dash, 2f);
        }
        g2.setStroke(awtStroke);

    }

    @Override
    public void drawString(String str, int x, int y) {
        g2.drawString(str, x, y);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        g2.drawRect(x, y, width, height);
    }


    @Override
    public void fillRect(int x, int y, int width, int height) {
        g2.fill(new Rectangle(x, y, width, height));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        g2.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        g2.fill(new Ellipse2D.Float(x, y, width, height));
    }

    @Override
    public void drawPoint(int x, int y) {
        g2.drawLine(x, y, x, y);
    }

    @Override
    public void drawPath(BPath path) {
        g2.draw(((AwtPath) path).getGeneralPath());
    }

    @Override
    public void fillPath(BPath path) {
        g2.fill(((AwtPath) path).getGeneralPath());
    }

    @Override
    public BPath getEmptyPath() {
        return new AwtPath();
    }

    @Override
    public void enableAntiAliasAndHinting() {
       /*
        * https://stackoverflow.com/questions/31536952/how-to-fix-text-quality-in-java-graphics
        */
        Map<?, ?> desktopHints =
                (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (desktopHints != null) {
            g2.setRenderingHints(desktopHints);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      /*  g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);  */

    }
}
