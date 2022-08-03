package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */
import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;

public class TImageButton extends TButton implements ImageObserver, ImageConsumer {

  public Image image;
  private Image hilightImage;
  public G g;
  public static final int DARKEN_AMT = 0x20;


  public TImageButton(String fileName, G gIn) {
    g = gIn;
    image = g.applet.getImage(g.applet.getDocumentBase(), fileName);
    int temp;
    temp = image.getWidth(this);
    if (temp > 0)
      mWidth = temp;
    temp = image.getHeight(this);
    if (temp > 0)
      mHeight = temp;

    ImageFilter darkenfilter = new DarkenFilter();
    hilightImage = g.applet.createImage(new FilteredImageSource(image.getSource(), darkenfilter));
    hilightImage.getSource().startProduction(this);

  }

  private class DarkenFilter extends ImageFilter {
    ColorModel originalModel;
    ColorModel directColor;
    int[]     pixelBuf;
    int        rmask = 0xFF0000;
    int        gmask = 0x00FF00;
    int        bmask = 0x0000FF;

    public DarkenFilter() {
      directColor = new DirectColorModel(24, rmask, gmask, bmask);
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model, byte pixels[], int off, int scansize) {
      int rgb;
      int j = 0;
      int rdarken = DARKEN_AMT * 0x10000;
      int gdarken = DARKEN_AMT * 0x00100;
      int bdarken = DARKEN_AMT;
      int pixel;
      pixelBuf = new int[pixels.length];
      for (int i = 0; i < pixels.length; i++) {
        rgb = model.getRGB((pixels[i] & 0xFF));
        pixelBuf[i]  = Math.max(0, (rgb & rmask) - rdarken)
                     + Math.max(0, (rgb & gmask) - gdarken)
                     + Math.max(0, (rgb & bmask) - bdarken);
      }

      try {
        super.setPixels(x, y, w, h, directColor, pixelBuf, off, scansize);
      }
       catch (Exception e) {
         System.out.println("Error in image coloring.");
       }
    }
  }

  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    if (mHighlight)
      graphics.drawImage(hilightImage, (int)x, (int)y, this);
    else
      graphics.drawImage(image, (int)x, (int)y, this);
  }

// Image observer function
  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    if ((infoflags & ImageObserver.WIDTH) != 0) {
      mWidth = width;
    }
    if ((infoflags & ImageObserver.HEIGHT) != 0) {
      mHeight = height;
    }
    if ((infoflags & ImageObserver.ALLBITS) != 0) {
      g.applet.repaint();
    }
    return true;
  }


  public void setDimensions(int width, int height) {}
  public void setProperties(Hashtable props) {}
  public void setColorModel(ColorModel model) {}
  public void setHints(int hintflags) {}
  public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {}
  public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {}
  public void imageComplete(int status) {}
}