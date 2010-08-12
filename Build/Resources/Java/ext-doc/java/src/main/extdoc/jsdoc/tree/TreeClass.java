package extdoc.jsdoc.tree;

import java.util.ArrayList;
import java.util.List;

import extdoc.jsdoc.docs.DocCustomTag;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 17:02:23
 */
public class TreeClass implements Comparable<TreeClass>{
    public boolean singleton;
    public String className;
    public String shortClassName;
    public boolean component;
    
    public List<DocCustomTag> customTags = new ArrayList<DocCustomTag>();

    public int compareTo(TreeClass o) {
        return shortClassName.compareTo(o.shortClassName);
    }
}

