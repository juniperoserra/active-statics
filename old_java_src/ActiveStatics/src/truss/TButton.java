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

// Button is not to be dragged. How about that?

public class TButton extends GraphicEntity {

  public static final int DEFAULT_SIZE = 10;
  public Color mHighlightColor = new Color(180, 180, 180);
  public static final int LEFT_MARGIN = 12;

  public boolean mDrawOutline = true;

  public Font mFont;
  public int mSize;
  private int mOldSize;
  private Point mHitPoint = new Point();
  public  String mText;
  public boolean mHighlight = false;
  public boolean mIsToggle = false;
  public boolean mSelected = false;
  public float x, y, mWidth, mHeight;
  public TAction mAction;
  private FontMetrics mMetrics;

  public TButton(String text) {
    mText = text;
    mSize = DEFAULT_SIZE;
    mOldSize = mSize;
    mFont = new Font("sanserif", Font.PLAIN, mSize);
    mMetrics = Toolkit.getDefaultToolkit().getFontMetrics(mFont);
    mWidth = mMetrics.stringWidth(mText) + 20;
    mPosRelativeTo = VIEW_RELATIVE;
  }

  public TButton() {
    mText = "";
    mSize = DEFAULT_SIZE;
    mOldSize = mSize;
    mFont = new Font("sanserif", Font.PLAIN, mSize);
    mMetrics = Toolkit.getDefaultToolkit().getFontMetrics(mFont);
  }

  public boolean isButton() {return true;}

  public void update() {
    if (mSize != mOldSize) {
      mFont = new Font("sanserif", Font.PLAIN, mSize);
      if (mWidth == mMetrics.stringWidth(mText) + 20) {
        mMetrics = Toolkit.getDefaultToolkit().getFontMetrics(mFont);
        mWidth = mMetrics.stringWidth(mText) + 20;
      }
      mOldSize = mSize;
    }
  }

  public void run() {
    if (mIsToggle)
      mSelected = !mSelected;
    if (mAction != null)
      mAction.run();
  }

  public boolean hit(Point p) {
    int totalSize = mSize;
    if (mInvisible)
      return false;

    if (p.x < x || p.x - x > mWidth)
      return false;
    if (p.y < y || p.y - y > mHeight)
      return false;
    return true;
  }

  public void draw(Graphics graphics) {
    if (mInvisible)
      return;

    if (mHighlight || mSelected) {
      graphics.setColor(mHighlightColor);
      graphics.fillRect((int)x, (int)y, (int)mWidth, (int)mHeight);
    }
    if (mDrawOutline) {
      graphics.setColor(Color.black);
      graphics.drawRect((int)x, (int)y, (int)mWidth, (int)mHeight);
    }

    graphics.setColor(mColor);
    graphics.setFont(mFont);
    graphics.drawString(mText, (int)x + LEFT_MARGIN, (int)y + mSize + ((int)mHeight - mSize) / 2);
  }

  public void getExtents(Rectangle extents) {
    extents.x = (int)x;
    extents.y = (int)y;
    extents.width = (int)mWidth;
    extents.height = (int)mHeight;
  }

}