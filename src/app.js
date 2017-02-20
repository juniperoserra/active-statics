/**
 * Created by simong on 2/20/17.
 */
console.log('hi there');

import { text } from './mod1';

window.addContent = function() {
    document.getElementById('main')
        .insertAdjacentHTML('afterbegin', `<div id="newChild">${text()}</div>`);
}