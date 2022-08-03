package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class TTextTriangle extends TText {

  public TPoint p1, p2, p3;
  public int xOff = 0;
  public int yOff = 0;

  public TTextTriangle() {
  }

  public void update() {
    super.update();
    x = (p1.x + p2.x + p3.x) / 3.0f;
    y = (p1.y + p2.y + p3.y) / 3.0f;
    x -= 10 * (mText.length() - 1);
    x += xOff;
    y += yOff;
  }
}