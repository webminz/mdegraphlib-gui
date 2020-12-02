
var waitForElement = function(time) {
    var el = document.getElementById("TEST");
    if (el != null)   {
        initializeEditor(el, "1", null);
    } else {
    setTimeout(function() {
        waitForElement(time)
    }, time);
    }
}

console.log("Registering JavaScript for graphical editor...");

import {initializeEditor} from './diaged/editor';

if (!window.startGraphicalEditor) {

window.startGraphicalEditor = function(container, instanceId, backendUrl) {
    initializeEditor(container, instanceId, backendUrl);
}

console.log("Graphical editor registered");


}

