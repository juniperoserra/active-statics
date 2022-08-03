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

public class TLineForcePoly extends TLine {

  public static final float ZERO_LENGTH = .3f;
  public static final int COMPRESSIVE = 1;
  public static final int TENSILE = -1;
  public static final int NONE = 0;

  public int mCharacter;

  public TPoint mMemberStart;
  public TPoint mMemberEnd;

  public static Color mColorCompressive;
  public static Color mColorTensile;
  public static Color mColorZero;

//  public G g;

  public TLineForcePoly() {//G aG) {
    mColorCompressive = G.mRed;
    mColorTensile = G.mBlue;
    mColorZero = G.mYellow;
    mSize = 3;
    //g = aG;
  }

  public void update() {
    super.update();
    if (length() <= ZERO_LENGTH) {
      mCharacter = NONE;
      mColor = mColorZero;
    }
    else if (Util.near(Util.direction(mMemberStart.x, mMemberStart.y, mMemberEnd.x, mMemberEnd.y),
        direction(), .02)) {
      mCharacter = TENSILE;
      mColor = mColorTensile;
    }
    else {
      mCharacter = COMPRESSIVE;
      mColor = mColorCompressive;
    }
  }
}