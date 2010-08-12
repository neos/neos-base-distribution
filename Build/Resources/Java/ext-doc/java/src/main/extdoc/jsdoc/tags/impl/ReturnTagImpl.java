package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.ReturnTag;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 2:16:53
 */
class ReturnTagImpl extends TagImpl implements ReturnTag {

    private String returnType;

    private String returnDescription;

    public ReturnTagImpl(String name, String text) {
        super(name, text);
        String[] str = divideAtWhite(text, 2);
        returnType = removeBrackets(str[0]);
        returnDescription = str[1];
    }

    public String getReturnType() {
        return returnType;
    }

    public String getReturnDescription() {
        return returnDescription;
    }
}
