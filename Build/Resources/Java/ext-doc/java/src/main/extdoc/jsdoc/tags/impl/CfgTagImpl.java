package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.CfgTag;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 1:58:03
 */
class CfgTagImpl extends TagImpl implements CfgTag {

    private String cfgName;

    private String cfgType;

    private String cfgDescription;

    private boolean optional;

    public CfgTagImpl(String name, String text) {
        super(name, text);
        String[] str = divideAtWhite(text, 3);
        cfgType = removeBrackets(str[0]);
        cfgName = str[1];
        optional = isOptional(str[2]);
        cfgDescription = optional?cutOptional(str[2]):str[2];

    }

    public String getCfgName() {
        return cfgName;
    }

    public String getCfgType() {
        return cfgType;
    }

    public String getCfgDescription() {
        return cfgDescription;
    }

    public boolean isOptional() {
        return optional;
    }
}
