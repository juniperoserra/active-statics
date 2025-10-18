/**
 * Created by simong on 2/20/17.
 */
import { init } from './graphics/Paper';
import Graphics from './graphics/Graphics';
import Scene from './graphics/Scene';
import LauncherApp from './apps/LauncherApp';

window.startApp = function() {
    init();
    const graphics = new Graphics();
    const launcherApp = new LauncherApp(new Scene(graphics));
}