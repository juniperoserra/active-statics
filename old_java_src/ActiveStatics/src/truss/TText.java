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

// Text is not to be dragged. How about that?

public class TText extends GraphicEntity {

  public static final int DEFAULT_SIZE = 12;
  public Font mFont;
  public FontMetrics mMetrics;
  public int mSize;
  private int mOldSize;
  private Point mHitPoint = new Point();
  private Point mDragOffset = new Point();
  public  String mText;
  public float x, y;
  public String mExponent;
  public Font mExpFont;

  public TText() {
    mText = "";
    mSize = DEFAULT_SIZE;
    mOldSize = mSize;
    mFont = new Font("sanserif", Font.PLAIN, mSize);
    mMetrics = Toolkit.getDefaultToolkit().getFontMetrics(mFont);
  }

  public void update() {
    if (mSize != mOldSize) {
      mFont = new Font("sanserif", Font.PLAIN, mSize);
      mMetrics = Toolkit.getDefaultToolkit().getFontMetrics(mFont);
      mOldSize = mSize;
    }
  }

  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    graphics.setColor(mColor);
    graphics.setFont(mFont);
    graphics.drawString(mText, (int)x, (int)y);

    if (mExponent != null) {
      if (mExpFont == null) {
        mExpFont = new Font("sanserif", Font.PLAIN, Math.max(9, mSize - 6));
      }
      graphics.setFont(mExpFont);
      graphics.drawString(mExponent, (int)x + mMetrics.stringWidth(mText), (int)(y - 0.33 * mMetrics.getHeight()));
    }
  }


  public void getExtents(Rectangle extents) {
    extents.x = (int)x;
    extents.y = (int)y;
    extents.width = mMetrics.stringWidth(mText);
    extents.height = mMetrics.getHeight();
  }
}