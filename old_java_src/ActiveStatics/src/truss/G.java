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
import java.applet.*;

public class G {

  public static final String FORCE_UNIT = "";
  public static final String LENGTH_UNIT = " m";

  public static final int WINDOW_X_START = 20;
  public static final int WINDOW_Y_START = 20;
 public static final int WINDOW_X_INC = 24;
  public static final int WINDOW_Y_INC = 24;

  public static int windowX = WINDOW_X_START;
  public static int windowY = WINDOW_Y_START;
  public static int WINDOW_X_START_MAX = 620;
  public static int WINDOW_Y_START_MAX = 400;
  public static int windowStacks = 0;

  public Container mFrame;
  public static final Font mLabelFont = new Font("sansserif", Font.PLAIN, 14);
  public GraphicEntity selectedEntity;

  public Timer mTimer;
  public boolean  autoMove = false;

  public static final  Color mRed = new Color(0xCC, 0x33, 0x00);
  public static final Color mGreen = new Color(10, 120, 10);
  public static final Color mBlue = new Color(0x33, 0x33, 0xCC);
  public static final Color mYellow = new Color(0xDD, 0xBB, 0x33);
  public static final Color mBackground = new Color(235, 225, 205);
  public static final Color mControlPointColor = new Color(225, 160, 40);

  public float mLengthDivisor = 1.0f;
  public Applet applet;

  public int mXOff, mYOff;
  public boolean mouseDown = false;

  //public static GraphicEntity[] drawList = new GraphicEntity[0];
  //public static GraphicEntity[] updateList = new GraphicEntity[0];

  public G(Applet appletIn) {
    applet = appletIn;
//    mLabelFont = new Font("sansserif", Font.PLAIN, 14);
  }
}