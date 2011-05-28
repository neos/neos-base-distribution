/**
 * Selenium User Extensions.
 *
 * Contain the following Selenium Fixes:
 * 1) Bugfix for key event listeners in Chrome
 */

/**
 * BEGIN 1) Bugfix for key event listeners in Chrome
 *
 * Taken from: http://code.google.com/p/selenium/issues/detail?id=567
 */

// in webkit, events created with document.createEventObject('UIEvents') have read-only
// keyCode properties. This monkey-patches the selenium JS to use createEvent('Events')
// for some terrific research, see http://developer.yahoo.com/yui/docs/UserAction.js.html

window['triggerKeyEvent'] = function(element, eventType, keySequence, canBubble, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown) {
    var keycode = getKeyCodeFromKeySequence(keySequence);
    canBubble = (typeof(canBubble) === undefined) ? true : canBubble;
    if (element.fireEvent && element.ownerDocument && element.ownerDocument.createEventObject) { // IE
        var keyEvent = createEventObject(element, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown);
        keyEvent.keyCode = keycode;
        element.fireEvent('on' + eventType, keyEvent);
    }
    else {
        var evt;
        if (window.KeyEvent) {
            evt = document.createEvent('KeyEvents');
            evt.initKeyEvent(eventType, true, true, window, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown, keycode, keycode);
        } else {
            evt = document.createEvent('Events');

            evt.shiftKey = shiftKeyDown;
            evt.metaKey = metaKeyDown;
            evt.altKey = altKeyDown;
            evt.ctrlKey = controlKeyDown;

            evt.initEvent(eventType, true, true);
            evt.keyCode = parseInt(keycode);

        }

        element.dispatchEvent(evt);
    }
}

/**
 * END 1) Bugfix for key event listeners in Chrome
 */