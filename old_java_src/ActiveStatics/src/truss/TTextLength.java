package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class TTextLength extends TText {

  public TLineForcePoly mForcePolyLine;
  public String mPrefix = "";
  public String mPostfix = G.FORCE_UNIT;

  public G g;

  public TTextLength(G aG) {
    g = aG;
  }

  public void update() {
    super.update();
    mText = mPrefix + Util.round(mForcePolyLine.length() / g.mLengthDivisor, 1) + mPostfix;
    if (mForcePolyLine.mCharacter == TLineForcePoly.COMPRESSIVE)
      mText += " C";
    else if (mForcePolyLine.mCharacter == TLineForcePoly.TENSILE)
      mText += " T";
    mColor = mForcePolyLine.mColor;
  }
}