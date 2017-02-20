/**
 * Created by simong on 2/20/17.
 */
console.log('hi there');

require("script-loader!paper");

import { text } from './mod1';

window.startApp = function() {
    document.getElementById('main')
        .insertAdjacentHTML('afterbegin', `<div id="newChild">${text()}</div>`);
}