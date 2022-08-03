package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class TTextPointLength extends TTextPoint {

  public TLine mLine;
  public String mPrefix = "";
  public String mPostfix = G.FORCE_UNIT;

  public G g;

  public TTextPointLength(G aG) {
    g = aG;
  }

  public void update() {
    super.update();
    mText = mPrefix + Util.round(mLine.length() / g.mLengthDivisor, 1) + mPostfix;
  }
}