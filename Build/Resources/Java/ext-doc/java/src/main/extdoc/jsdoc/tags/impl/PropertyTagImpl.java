package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.PropertyTag;

/**
 * User: Andrey Zubkov
 * Date: 27.11.2008
 * Time: 23:58:06
 */
class PropertyTagImpl extends TagImpl implements PropertyTag {

    private String propertyName;

    private String propertyDescription;

    public PropertyTagImpl(String name, String text) {
        super(name, text);        
        String[] str = divideAtWhite(text, 2);
        propertyName = str[0];
        propertyDescription = str[1];
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }
}
