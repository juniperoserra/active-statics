package truss;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;

import java.awt.Canvas;

/**
 * <p>Title: Truss</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Simon Greenwold
 * @version 1.0
 */

public class NoScrollUpdateCanvas extends Canvas {

  public static final int MAX_VIEW_X_OFFSET = 8000;
  public static final int MAX_VIEW_Y_OFFSET = -8000;
  public static final int MIN_VIEW_X_OFFSET = 8000;
  public static final int MIN_VIEW_Y_OFFSET = -8000;

  public int viewXOffset = 0;
  public int viewYOffset = 0;

  private Image mOffscreen;
  private Graphics mGOff;
  Dimension mViewSize = new Dimension();

  private TButton mouseOverButton;
  private Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
  private Cursor arrow = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

  public int mUpdateTimes = 2;
  public boolean mGlobalUpdateEveryTime = false;
  private int paintedTwice = 0;

  private Point tempPoint = new Point();
  private Point mouseDownOffset = new Point();

  public GraphicEntity[] drawList;
  public GraphicEntity[] updateList;

  public G g;

  public Applet parentApplet;

  /**Construct the panel*/
  public NoScrollUpdateCanvas(Applet parent, G aG) {
    parentApplet = parent;
    setSize(parentApplet.getSize());
    g = aG;
    init();
  }

  public void mouseUpHook() {
  }

  public void predrawViewHook(Graphics graphics) {
  }

  public void predrawGlobalHook(Graphics graphics) {
  }

  public void init() {
    setBackground(G.mBackground);

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        g.mouseDown = true;
        tempPoint.x = e.getX();
        tempPoint.y = e.getY();
        mouseDownOffset.x = viewXOffset;
        mouseDownOffset.y = viewYOffset;
        for (int i = drawList.length - 1; i >= 0; i--) {
          if (drawList[i].hit(tempPoint, viewXOffset, viewYOffset)) {
            g.selectedEntity = drawList[i];
            if (g.selectedEntity.isButton()) {
              ( (TButton) g.selectedEntity).mHighlight = true;
              repaint();
            }
            return;
          }
        }
        g.selectedEntity = null;
      }

      public void mouseReleased(MouseEvent e) {
        g.mouseDown = false;
        mouseUpHook();
        if (g.selectedEntity == null)
          return;
        if (!g.selectedEntity.isButton())
          return;
        ((TButton)g.selectedEntity).mHighlight = false;
        tempPoint.x = e.getX();
        tempPoint.y = e.getY();
        boolean releasedIn = ((TButton)g.selectedEntity).hit(tempPoint, viewXOffset, viewYOffset);
        if (releasedIn)
          ((TButton)g.selectedEntity).run();
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        tempPoint.x = e.getX();
        tempPoint.y = e.getY();
        for (int i = drawList.length - 1; i >= 0; i--) {
          if (drawList[i].isButton() && !drawList[i].mInvisible) {
            if (drawList[i].hit(tempPoint, viewXOffset, viewYOffset)) {
              setCursor(hand);
              return;
            }
          }
        }
        setCursor(arrow);
      }

      public void mouseDragged(MouseEvent e) {
        if (g.selectedEntity == null) {
          viewXOffset = mouseDownOffset.x - tempPoint.x + e.getX();
          viewYOffset = mouseDownOffset.y - tempPoint.y + e.getY();
          repaint();
        }
        else {
          if (g.selectedEntity.isButton()) {
            tempPoint.x = e.getX();
            tempPoint.y = e.getY();

            boolean stillIn = ((TButton)g.selectedEntity).hit(tempPoint, viewXOffset, viewYOffset);
            ((TButton)g.selectedEntity).mHighlight = stillIn;
          }
          else {
            tempPoint.x = e.getX();
            tempPoint.y = e.getY();
            g.selectedEntity.dragged(tempPoint, viewXOffset, viewYOffset);
          }
          repaint();
        }
      }

    });

  }


  public void appletResized() {
    if (mGOff != null) {
      mGOff.dispose();
      mGOff = null;
    }
    if (mOffscreen != null) {
      mOffscreen.flush();
      mOffscreen = null;
    }
    System.gc();

    mViewSize.width = parentApplet.getSize().width;
    mViewSize.height = parentApplet.getSize().height;
    mOffscreen = createImage(mViewSize.width, mViewSize.height);
    mGOff = mOffscreen.getGraphics();
    mGOff.setFont(getFont());

    setSize(parentApplet.getSize());

    update(getGraphics());
  }

  public void globalUpdate() {    // Apply contstraints
    // Override this anonymously.
  }

  public void update(Graphics graphics) {
    if (mGOff == null)
      appletResized();

    //System.out.println("In update.");
    if (!mGlobalUpdateEveryTime)
      globalUpdate();

    //System.out.println("In update. W: " + W);

    for (int j = 0; j < mUpdateTimes; j++) {  // We update j times to filter through the changes
      if (mGlobalUpdateEveryTime)
        globalUpdate();
      for (int i = 0; i < updateList.length; i++) {
        updateList[i].update();
      }
    }

    mGOff.clearRect(0, 0, mViewSize.width, mViewSize.height);
//    mGOff.drawRect(0, 0, mViewSize.width, mViewSize.height);


    predrawViewHook(mGOff);

//    if (mBeamLoadApplet != null)
//      mBeamLoadApplet.drawStuff(mGOff);

    for (int i = 0; i < drawList.length; i++) {
      if (drawList[i].mPosRelativeTo == GraphicEntity.VIEW_RELATIVE)
        drawList[i].draw(mGOff);
    }
    int tmpXOff = viewXOffset;
    int tmpYOff = viewYOffset;
    mGOff.translate(tmpXOff, tmpYOff);
    predrawGlobalHook(mGOff);
    for (int i = 0; i < drawList.length; i++) {
      if (drawList[i].mPosRelativeTo == GraphicEntity.GLOBAL_RELATIVE)
        drawList[i].draw(mGOff);
    }
    mGOff.translate(-tmpXOff, -tmpYOff);



    paint(graphics);
  }

  public void paint(Graphics graphics) {
    if (paintedTwice < 2) {
      for (int i = 0; i < updateList.length; i++) {
        updateList[i].update();
      }
      for (int i = 0; i < updateList.length; i++) {
        updateList[i].update();
      }
      predrawViewHook(graphics);
      predrawGlobalHook(graphics);
      for (int i = 0; i < drawList.length; i++) {
        drawList[i].draw(graphics);
      }
      paintedTwice++;
    }
    else {
      //System.out.println("Offscreen " + mOffscreen);
      graphics.drawImage(mOffscreen, 0, 0, this);
    }
  }


}