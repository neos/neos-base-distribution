package extdoc.jsdoc.processor;

import extdoc.jsdoc.docs.Doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Andrey Zubkov
 * Date: 14.12.2008
 * Time: 4:38:27
 */
class DocFile {
    public String fileName;
    public String targetFileName;
    public File file;
    List<Doc> docs = new ArrayList<Doc>();
}
