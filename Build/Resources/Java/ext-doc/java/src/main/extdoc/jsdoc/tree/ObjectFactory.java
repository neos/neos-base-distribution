package extdoc.jsdoc.tree;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * User: Andrey Zubkov
 * Date: 02.11.2008
 * Time: 4:25:56
 */
@XmlRegistry
public class ObjectFactory {
        public ObjectFactory() {
    }

    public TreePackage createTreePackage(){
        return new TreePackage();   
    }
}
