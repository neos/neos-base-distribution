package extdoc.jsdoc.docs;

/**
 * User: Andrey Zubkov
 * Date: 03.11.2008
 * Time: 2:23:50
 */
public abstract class DocAttribute extends Doc
                                        implements Comparable<DocAttribute>{
    public String name;
    public Description description;
    public String className;
    public String shortClassName;
    public boolean isStatic; 

    public  int compareTo(DocAttribute anotherAttribute) {
        // name may be null or anotherAttribute may be null
        // safe comparison
        return (name!=null && 
                anotherAttribute!=null &&
                anotherAttribute.name!=null)?
                name.compareTo(anotherAttribute.name):0;
    }

}
