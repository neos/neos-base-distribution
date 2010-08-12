package extdoc.jsdoc.docs;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Andrey Zubkov
 * Date: 14.12.2008
 * Time: 1:14:24
 */
public abstract class Doc {
    public List<DocCustomTag> customTags = new ArrayList<DocCustomTag>();
    public boolean hide;
    @XmlTransient
    public long positionInFile;
    @XmlTransient
    public String id;
    public String href;
}
