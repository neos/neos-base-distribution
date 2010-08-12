package extdoc.jsdoc.docs;

/**
 * User: Andrey Zubkov
 * Date: 03.11.2008
 * Time: 19:04:43
 */
public class ClassDescr implements Comparable<ClassDescr>{
    public String className;
    public String shortClassName;

    public int compareTo(ClassDescr o) {
        return className.compareTo(o.className);
    }
}
