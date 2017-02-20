/**
 * Created by simong on 2/20/17.
 */

import AppBase from './AppBase';
import TPoint from '../graphics/TPoint';

export default class SinglePanelApp extends AppBase {

    static APPLET_WIDTH = 620;
    static APPLET_HEIGHT = 620;
    static PANEL_SIZE = 180;
    static TRUSS_X_START = 160;
    static TRUSS_Y_START = 350;
    static START_FORCE_LENGTH = 165;
    static LOAD_LINE_START_X = 480;
    static LOAD_LINE_START_Y = 120;
    static MAX_WIDTH = 60.0;
    static MIN_WIDTH = 2.0;
    static WIDTH_MULT = 0.1;
    static REPORT_X_START = 400;
    static REPORT_Y_START = 440;
    static REPORT_LINE_SPACE = 17;
    static REPORT_COLUMN_SPACE = 110;
    static BUTTON_START_X = 20;
    static BUTTON_START_Y = 70;
    static BUTTON_Y_OFFSET = 30;

    constructor(graphics) {
        super(graphics,
            [SinglePanelApp.APPLET_WIDTH, SinglePanelApp.APPLET_HEIGHT]);

        //this.makeButtons();
        this.makeNodes();
        //this.makeMembers();
        //this.makeLoads();
        //this.addNodes();
        //this.makeRb();
        //this.makeLoadLine();
        //this.makeRa();
        //this.makeForcePolygon();
        //this.makeTriangleLabels();
        //this.makeText();
        //this.makeSupports();
        //this.makeReport();
        //this.makeLinesOfAction();
    }

    makeNodes() {
        this.node1 = new TPoint(this.graphics, 20, 20);
    }

}