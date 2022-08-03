package truss;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;

/**
 * <p>Title: Truss</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Simon Greenwold
 * @version 1.0
 */

public class SilentLauncherApplet extends Applet {

  public static Frame[] frames = new Frame[8];

  public static final int APPLET_WIDTH = 7;
  public static final int APPLET_HEIGHT = 7;

  G g;

  boolean isStandalone = false;
  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }


  public void launchSinglePanel() {
    SinglePanelApplet applet = new SinglePanelApplet();
    applet.isStandalone = true;
    if (frames[0] == null) {
      frames[0] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  1. Single Panel Truss", null, 0);
    }
    else {
      frames[0].show();
    }
  }

  public void launchSimpleTruss() {
    TrussApplet applet = new TrussApplet();
    applet.isStandalone = true;
    System.out.println("Launch simple truss.");
    if (frames[1] == null) {
      frames[1] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  2. Simple Truss", null, 1);
    }
    else {
      frames[1].show();
    }
  }

  public void launchHangingCable() {
    HangingCableApplet applet = new HangingCableApplet();
    applet.isStandalone = true;
    if (frames[2] == null) {
      frames[2] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  3. Hanging Cable/Arch", null, 2);
    }
    else {
      frames[2].show();
    }
  }

  public void launchCantilever() {
    CantileverApplet applet = new CantileverApplet();
    applet.isStandalone = true;
    if (frames[3] == null) {
      frames[3] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  4. Cantilever Truss", null, 3);
    }
    else {
      frames[3].show();
    }
  }

  public void launchFanlike() {
    CableStayApplet applet = new CableStayApplet();
    applet.isStandalone = true;
    if (frames[4] == null) {
      frames[4] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  5. Fanlike Structure", null, 4);
    }
    else {
      frames[4].show();
    }
  }

  public void launchOverhanging() {
    OverhangApplet applet = new OverhangApplet();
    applet.isStandalone = true;
    if (frames[5] == null) {
      frames[5] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  6. Overhanging Truss", null, 5);
    }
    else {
      frames[5].show();
    }
  }

  public void launchMinWeight() {
    MinWeightApplet applet = new MinWeightApplet();
    applet.isStandalone = true;
    if (frames[6] == null) {
      frames[6] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  7. Minimum Weight Truss", null, 6);
    }
    else {
      frames[6].show();
    }
  }

  public void launchBeamLoad() {
    BeamLoadApplet applet = new BeamLoadApplet();
    applet.isStandalone = true;
    if (frames[7] == null) {
      frames[7] = openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  8. Beam Loading", null, 7);
    }
    else {
      frames[7].show();
    }
  }

  /**Initialize the applet*/
  public void init() {
    g = new G(this);
    for (int i = 0; i < 8; i++) {
      frames[i] = null;
    }
  }

  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Get parameter info*/
  public String[][] getParameterInfo() {
    return null;
  }

  public Frame openFrame(Applet applet, int width, int height, String name, Image iconImage, int frameNum) {
    Frame frame;
    frame = new Frame() {
      protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
          for (int i = 0; i < 8; i++) {
            if (frames[i] == this) {
              frames[i] = null;
              break;
            }
          }
          dispose();
        }
      }
      public synchronized void setTitle(String title) {
        super.setTitle(title);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      }
    };
    frame.setTitle(name);
    frame.add(applet, BorderLayout.CENTER);
    applet.init();
    applet.start();
    frame.setSize(width, height);

    //Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    //g.WINDOW_X_START_MAX = d.width - 400;
    //g.WINDOW_Y_START_MAX = d.height - 400;

    //frame.setLocation(g.windowX, g.windowY);
    frame.setLocation(10 + g.WINDOW_X_INC * frameNum, 10 + g.WINDOW_Y_INC * frameNum);
    frame.setIconImage(iconImage);
    /*g.windowX += g.WINDOW_X_INC;
    g.windowY += g.WINDOW_Y_INC;
    if (g.windowX > g.WINDOW_X_START_MAX || g.windowY > g.WINDOW_Y_START_MAX) {
      g.windowY = g.WINDOW_Y_START;
      g.windowStacks += 3;
      if (g.windowStacks * g.WINDOW_X_INC >= g.WINDOW_X_START_MAX)
        g.windowStacks = 0;
      g.windowX = g.WINDOW_X_START + (g.windowStacks * g.WINDOW_X_INC);
    }*/
    frame.setVisible(true);
    return frame;
  }
}