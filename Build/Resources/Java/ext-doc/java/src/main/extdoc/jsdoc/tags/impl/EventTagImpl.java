package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.EventTag;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:04:58
 */
class EventTagImpl extends TagImpl implements EventTag {

    private String eventName;

    private String eventDescription;

    public EventTagImpl(String name, String text) {
        super(name, text);
        String[] str = divideAtWhite(text, 2);
        eventName = str[0];
        eventDescription = str[1];
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }
}
