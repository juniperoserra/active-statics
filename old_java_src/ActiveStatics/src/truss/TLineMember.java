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

public class TLineMember extends TLine {

  public static final float MAX_WIDTH = 60.0f;
  public static final float MIN_WIDTH = 2.0f;
  public static final float WIDTH_MULT = 0.1f;

  public static Color mColorCompressive;
  public static Color mColorTensile;
  public static Color mColorZero;
  public boolean mIsWeightMember = false;

  public TLineForcePoly mForcePolyMember;

  public G g;

  public TLineMember(G aG) {
    mColorCompressive = G.mRed;
    mColorTensile = G.mBlue;
    mColorZero = G.mYellow;
    g = aG;
  }

  public void update() {
    super.update();
    if (mForcePolyMember == null)
      return;
    if (mForcePolyMember.mCharacter == mForcePolyMember.NONE) {
      mColor = mColorZero;
    }
    else if (mForcePolyMember.mCharacter == mForcePolyMember.COMPRESSIVE) {
      mColor = mColorCompressive;
    }
    else {
      mColor = mColorTensile;
    }

    if (!mIsWeightMember)
      mSize = (int)Util.bound(mForcePolyMember.length() * WIDTH_MULT, MIN_WIDTH, MAX_WIDTH);
    else
      mSize = (int)Util.bound((mForcePolyMember.length() *
                   length() / (g.mLengthDivisor * g.mLengthDivisor))* WIDTH_MULT, MIN_WIDTH, MAX_WIDTH);
  }
}