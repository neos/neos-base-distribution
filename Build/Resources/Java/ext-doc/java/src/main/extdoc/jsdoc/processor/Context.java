package extdoc.jsdoc.processor;

import extdoc.jsdoc.docs.*;
import extdoc.jsdoc.schema.Tag;
import extdoc.jsdoc.tree.TreePackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Andrey Zubkov
 * Date: 14.12.2008
 * Time: 4:39:33
 */
class Context {

    private final static String TARGET_FILE_EXTENSION = "html";

    long position = 0;

    long lastCommentPosition = 0;

    private DocClass currentClass = null;

    private DocFile currentFile = null;

    private List<DocFile> docFiles = new ArrayList<DocFile>();

    private List<DocClass> classes = new ArrayList<DocClass>();

    private List<DocCfg> cfgs = new ArrayList<DocCfg>();

    private List<DocProperty> properties =
            new ArrayList<DocProperty>();

    private List<DocMethod> methods = new ArrayList<DocMethod>();

    private List<DocEvent> events = new ArrayList<DocEvent>();

    private TreePackage tree = new TreePackage();

    private List<extdoc.jsdoc.schema.Tag> customTags
                = new ArrayList<extdoc.jsdoc.schema.Tag>();


    public List<DocFile> getDocFiles() {
        return docFiles;
    }

    public List<DocClass> getClasses() {
        return classes;
    }

    public List<DocCfg> getCfgs() {
        return cfgs;
    }

    public List<DocProperty> getProperties() {
        return properties;
    }

    public List<DocMethod> getMethods() {
        return methods;
    }

    public List<DocEvent> getEvents() {
        return events;
    }

    public TreePackage getTree() {
        return tree;
    }

    public List<Tag> getCustomTags() {
        return customTags;
    }

    public void addDocFile(DocFile docFile) {
        docFiles.add(docFile);
    }

    public void addDocClass(DocClass docClass) {
        docClass.positionInFile = lastCommentPosition;
        docClass.id = "cls-" + docClass.className;
        docClass.href = currentFile.targetFileName + '#' + docClass.id;
        currentFile.docs.add(docClass);
        currentClass = docClass;
        classes.add(docClass);
    }

    public void addDocCfg(DocCfg docCfg) {
        docCfg.positionInFile = lastCommentPosition;
        docCfg.id = "cfg-" + docCfg.className +'-' + docCfg.name;
        docCfg.href = currentFile.targetFileName + '#' + docCfg.id;
        currentFile.docs.add(docCfg);
        cfgs.add(docCfg);
    }

    public void addDocProperty(DocProperty docProperty) {
        docProperty.positionInFile = lastCommentPosition;
        docProperty.id = "prop-" +  docProperty.className +'-' + docProperty.name;
        docProperty.href = currentFile.targetFileName + '#' + docProperty.id;
        currentFile.docs.add(docProperty);
        properties.add(docProperty);
    }

    public void addDocMethod(DocMethod docMethod) {
        docMethod.positionInFile = lastCommentPosition;
        docMethod.id = "method-" + docMethod.className +'-' + docMethod.name;
        docMethod.href = currentFile.targetFileName + '#' + docMethod.id;
        for(DocMethod method:methods){
                if(method.id.equals(docMethod.id)){
                        methods.remove(method);
                        currentFile.docs.remove(method);
                        break;
                }
        }
        currentFile.docs.add(docMethod);
        methods.add(docMethod);
    }

    public void addDocEvent(DocEvent docEvent) {
        docEvent.positionInFile = lastCommentPosition;
        docEvent.id = "event-" + docEvent.className +'-'+ docEvent.name;
        docEvent.href = currentFile.targetFileName + '#' + docEvent.id;
        currentFile.docs.add(docEvent);
        events.add(docEvent);
    }

    public void addClassToTree(DocClass docClass) {
        tree.addClass(docClass);
    }    

    public void sortTree(){
        tree.sort();
    }

    public void setCustomTags(List<Tag> customTags) {
        this.customTags = customTags;
    }

     public void setCurrentClass(DocClass currentClass){
        this.currentClass=currentClass;
    }
    
    public DocClass getCurrentClass(){
        return currentClass;        
    }

    public DocFile getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        DocFile docFile = new DocFile();
        docFile.fileName = currentFile.getName();
        docFile.file = currentFile;

        // check if file with the/ same name was already processed
        // ex.: "ext/widgets/Panel.js" and "other/Panel.js"
        // one of these files should be Panel.html another Panel1.html
        int sameNameCount = 0;
        for(DocFile f: docFiles){
            if(f.fileName.equals(docFile.fileName)){
                sameNameCount++;
            }
        }
        int lastDot = docFile.fileName.lastIndexOf('.');
        String name = lastDot!=-1?
                docFile.fileName.substring(0, lastDot):docFile.fileName;
        docFile.targetFileName = sameNameCount>0?
                name+sameNameCount+'.'+TARGET_FILE_EXTENSION:
                name+'.'+TARGET_FILE_EXTENSION;
                
        this.currentFile = docFile;
        addDocFile(this.currentFile);
    }
}
