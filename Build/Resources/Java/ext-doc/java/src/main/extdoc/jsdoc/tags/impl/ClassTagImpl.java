package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.ClassTag;

/**
 * User: Andrey Zubkov
 * Date: 31.10.2008
 * Time: 0:08:37
 */
class ClassTagImpl extends TagImpl implements ClassTag {

    String className;

    String classDescription;

    public ClassTagImpl(String name, String text) {
        super(name, text);
        String[] str = divideAtWhite(text, 2);
        className = str[0];
        classDescription = str[1];
    }

    public String getClassName() {
        return className;
    }

    public String getClassDescription() {
        return classDescription;
    }
}
