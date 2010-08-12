package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.TypeTag;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 2:07:54
 */
class TypeTagImpl extends TagImpl implements TypeTag {

    private String type;

    public TypeTagImpl(String name, String text) {
        super(name, text);
        type = removeBrackets(text);        
    }

    public String getType() {
        return type;
    }
}
