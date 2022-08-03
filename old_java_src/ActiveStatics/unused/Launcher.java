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
import java.awt.event.*;

public class Launcher extends Applet {

  //Frame singlePanel;

  public static final int APPLET_WIDTH = 700;
  public static final int APPLET_HEIGHT = 600;

  Button mSinglePanelButton;
  Button mTrussButton;
  Button mHangingButton;
  Button mCantButton;
  Button mCableButton;
  Button mOverButton;
  Button mMinWeightButton;
  Button mBeamLoadingButton;

  public Launcher() {
    setLayout(new GridLayout(8,1,0,0));
    mSinglePanelButton = new Button("SinglePanel");
    mTrussButton = new Button("Truss");
    mHangingButton = new Button("Hanging Cable/Arch");
    mCantButton = new Button("Cantilever Truss");
    mCableButton = new Button("Fanlike structure");
    mOverButton = new Button("Overhanging Truss");
    mMinWeightButton = new Button("Minimum Weight Truss");
    mBeamLoadingButton = new Button("Beam Loading");



    add(mSinglePanelButton);
    add(mTrussButton);
    add(mHangingButton);
    add(mCantButton);
    add(mCableButton);
    add(mOverButton);
    add(mMinWeightButton);
    add(mBeamLoadingButton);

  }

  public boolean action(Event ev, Object arg) {
    if (ev.target == mSinglePanelButton) {
      SinglePanelApplet applet = new SinglePanelApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mTrussButton) {
      TrussApplet applet = new TrussApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mHangingButton) {
      HangingCableApplet applet = new HangingCableApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mCantButton) {
      CantileverApplet applet = new CantileverApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mCableButton) {
      CableStayApplet applet = new CableStayApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mMinWeightButton) {
      MinWeightApplet applet = new MinWeightApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mOverButton) {
      OverhangApplet applet = new OverhangApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }
    else if (ev.target == mBeamLoadingButton) {
      BeamLoadApplet applet = new BeamLoadApplet();
      applet.isStandalone = true;
      openFrame(applet);
    }

    return false;
  }

  public void openFrame(Applet applet) {
    Frame frame;
    frame = new Frame() {
      protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
          dispose();
        }
      }
      public synchronized void setTitle(String title) {
        super.setTitle(title);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      }
    };
    frame.setTitle("Single Panel Truss");
    frame.add(applet, BorderLayout.CENTER);
    applet.init();
    applet.start();
    frame.setSize(APPLET_WIDTH,APPLET_HEIGHT + 20);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
  }

}