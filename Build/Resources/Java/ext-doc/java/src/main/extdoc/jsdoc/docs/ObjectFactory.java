package extdoc.jsdoc.docs;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * User: Andrey Zubkov
 * Date: 28.10.2008
 * Time: 22:09:36
 */
@XmlRegistry
public class ObjectFactory {
    
    public ObjectFactory() {
    }

    public DocClass createDocClass(){
        return new DocClass();   
    }
}
