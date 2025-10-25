export const hangingCableInstructions = `
<h1>Hanging Cable</h1>

<h2>The Screen</h2>

<p>The <b>Form Diagram</b> shows a cable suspended between two supports, carrying seven vertical loads.
    The cable takes on a characteristic funicular shape. To the right is the <b>Load Line</b>, showing
    the vertical loads stacked end-to-end, and the <b>Force Polygon</b> with pole point O, which
    determines the shape of the cable.
</p>

<p><b>Cable vs. Arch:</b> When the cable is concave upward (sagging), it is in <b>tension</b> and
    shown in <b style="color: blue;">blue</b>. When the form inverts and becomes concave downward,
    it behaves as an <b>arch</b> in <b style="color: red;">compression</b> and is shown in
    <b style="color: red;">red</b>.
</p>

<p>The <b>Toggle Switches</b> include</p>
<ul>
    <li><em>Return to Start:</em> Clears away all changes and returns everything to the original position.</li>
    <li><em>Keep Supports Level:</em> Maintains both support points at the same vertical height.</li>
    <li><em>Equalize Loads:</em> Makes all seven loads equal in magnitude and evenly spaces the load points.</li>
    <li><em>Extend Lines of Action:</em> Shows extensions of the force lines from the force polygon,
        demonstrating how they intersect at the corresponding nodes of the cable.</li>
</ul>

<p><b>You may move any node that is marked with a yellow circle.</b> All other parts of the screen
    will change instantaneously to reflect the consequences of each move–the cable shape adjusts,
    the force polygon updates, and the reactions at the supports are recalculated.
</p>

<hr/>
<h3>Exercise One:</h3>
<h2>Play</h2>
<p>Use the mouse to play with the cable in any way that you like. Move some of the yellow circles
    to explore the possibilities and discover how the various features work. Try the various toggles.
</p>

<p>Notice how the cable shape changes as you move the loads or supports. Observe the relationship
    between the force polygon on the right and the cable shape on the left.
</p>

<hr/>
<h3>Exercise Two:</h3>
<h2>The Funicular Curve</h2>
<p><em>Toggles On</em>: Return to Start; Equalize Loads</p>
<p>A. With equal loads evenly spaced, observe the smooth funicular curve that the cable forms.</p>
<p>B. Move the left support up and down. What happens to the cable shape? What happens to the
    force polygon as the height difference between supports changes?</p>
<p>C. Move the left support left and right. How does the span affect the cable shape and the
    forces in the cable?</p>

<hr/>
<h3>Exercise Three:</h3>
<h2>Cable to Arch Transformation</h2>
<p><em>Toggles On</em>: Return to Start; Equalize Loads</p>
<p>A. Grab one of the interior load points and move it upward slowly, raising it above the support level.
</p>
<p>What happens when the cable inverts and crosses from concave upward to concave downward?
    Notice the color change from blue (tension) to red (compression).
</p>
<p>B. Continue moving nodes to explore the arch form. An inverted cable is structurally equivalent
    to an arch–the form is the same, but the forces are reversed in character.
</p>

<hr/>
<h3>Exercise Four:</h3>
<h2>Unequal Loads</h2>
<p><em>Toggles On</em>: Return to Start. Turn off Equalize Loads.</p>
<p>A. Move one of the load magnitude points (the yellow circles at the tops of the load arrows)
    up or down to change that load's magnitude.
</p>
<p>What happens to the cable shape when loads are unequal? How does the force polygon reflect
    this change?
</p>
<p>B. Create a very large load at one point. How does the cable respond? What happens to the
    forces in the adjacent cable segments?</p>

<hr/>
<h3>Exercise Five:</h3>
<h3>The Role of the Pole Point O</h3>
<p><em>Toggles On</em>: Return to Start; Equalize Loads; Extend Lines of Action</p>
<p>A. Move the pole point O (the yellow circle in the force polygon) horizontally left and right.
</p>
<p>What happens to the cable shape as O moves? Notice that the horizontal distance from O to the
    load line determines the horizontal component of cable tension.
</p>
<p>B. With "Extend Lines of Action" on, observe how the rays from the force polygon correspond
    to the cable segments in the form diagram. Each ray from O through a point on the load line
    determines the slope of the corresponding cable segment.
</p>

<hr/>
<h3>Exercise Six:</h3>
<h2>Support Reactions</h2>
<p><em>Toggles On</em>: Return to Start; Equalize Loads</p>
<p>A. Move the left support horizontally. Watch the reaction forces (shown as green arrows at the
    supports). How do the reactions change as the span changes?
</p>
<p>B. Turn on "Keep Supports Level" and move the left support left and right. With supports at
    the same height, what can you say about the vertical components of the reactions?
</p>
<p>C. Turn off "Keep Supports Level" and raise one support higher than the other. How does this
    affect the distribution of vertical reaction forces between the two supports?
</p>

<hr/>
<h3>Exercise Seven:</h3>
<h2>Load Position Effects</h2>
<p><em>Toggles On</em>: Return to Start.</p>
<p>A. Move one of the interior load application points horizontally along the cable.</p>
<p>How does changing where a load is applied (without changing its magnitude) affect the cable
    shape? What principle of statics does this illustrate?
</p>
<p>B. Try moving multiple load points to cluster loads in one area versus spreading them out.
    How does load distribution affect the overall cable form?
</p>

<hr/>
<h3>Exercise Eight:</h3>
<h2>The Relationship Between Form and Force</h2>
<p><em>Toggles On</em>: Return to Start; Equalize Loads; Extend Lines of Action</p>
<p>A. Observe the complete system: the cable form on the left, the load line in the middle,
    and the force polygon with its rays on the right.
</p>
<p>The hanging cable is a fundamental demonstration of graphic statics: the force polygon
    (a diagram of forces) directly determines the geometric form. The funicular curve is
    uniquely defined by the loads and the position of pole point O.
</p>
<p>B. Experiment freely with all the movable points. Can you create a nearly horizontal cable?
    What happens to the forces when you do? Can you create a very steep cable? What are the
    limits of the possible forms?
</p>
`;
