package extdoc.jsdoc.processor;

import extdoc.jsdoc.docs.*;
import extdoc.jsdoc.tags.*;
import extdoc.jsdoc.tags.impl.Comment;
import extdoc.jsdoc.tplschema.*;
import extdoc.jsdoc.util.StringUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.*;
import java.util.regex.Pattern;



/**
 * User: Andrey Zubkov
 * Date: 25.10.2008
 * Time: 4:41:12
 */
public class FileProcessor{

    private final Logger logger;

    private final Handler logHandler;

    private Context context = new Context();

    private final static String OUT_FILE_EXTENSION = "html";
    private final static boolean GENERATE_DEBUG_XML = false;
    private final static String COMPONENT_NAME = "Ext.Component";
    private final static String DEFAULT_TYPE = "Object";

    private static final String START_LINK = "{@link";    

    private static enum LinkStates {READ, LINK}

    private static final String
        MEMBER_REFERENCE_TPL =
            "<a href=\"output/{0}.html#{0}-{1}\" " +
                    "ext:member=\"{1}\" ext:cls=\"{0}\">{2}</a>";

    private static final String
        CLASS_REFERENCE_TPL =
            "<a href=\"output/{0}.html\" " +
                    "ext:cls=\"{0}\">{1}</a>";

    private static final int DESCR_MAX_LENGTH = 117;

    private static final String DEFAULT_MATCH = "*.js";
    private static final boolean DEFAULT_SKIPHIDDEN = true;

    private static final String ENCODING = "UTF8";

    public FileProcessor() {
        logger = Logger.getLogger("extdoc.jsdoc.processor");
        logger.setUseParentHandlers(false);
        logHandler = new ConsoleHandler();
        logHandler.setFormatter(new Formatter() {
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        logger.addHandler(logHandler);
    }

    public void setVerbose(){
        logger.setLevel(Level.FINE);
        logHandler.setLevel(Level.FINE);
    }

    public void setQuiet(){
        logger.setLevel(Level.OFF);
    }

    /**
     * Processes link content (between "{" and "}")
     * @param text Content, ex: "Ext.DomQuery#select"
     * @return Array of 2 Strings: long and short versions
     */
    private String[] processLink(String text) {
         StringUtils.ClsAttrName res = StringUtils.processLink(text);
         String longText, shortText;
         if (res.attr.isEmpty()) {
             // class reference
             String cls = res.cls;
             String name = res.name.isEmpty() ? res.cls : res.name;
             longText = MessageFormat.format(CLASS_REFERENCE_TPL, cls, name);
             shortText = name;
         } else {
             // attribute reference
             String cls = res.cls.isEmpty() ? context.getCurrentClass().className
                     : res.cls;
             String attr = res.attr;
             String name;
             if (res.name.isEmpty()) {
                 if (res.cls.isEmpty()) {
                     name = res.attr;
                 } else {
                     name = cls + '.' + res.attr;
                 }
             } else {
                 name = res.name;
             }
             longText = MessageFormat.format(MEMBER_REFERENCE_TPL, cls, attr,
                     name);
             shortText = name;
         }
         return new String[] { longText, shortText };
     }



    private Description inlineLinks(String content){
        return inlineLinks(content, false);
    }    

    /**
     * Replaces inline tag @link to actual html links and returns shot and/or
     * long versions.
     *
     * @param cnt
     *            description content
     * @param alwaysGenerateShort
     *            forces to generate short version for Methods and events
     * @return short and long versions
     */
    private Description inlineLinks(String cnt, boolean alwaysGenerateShort) {
        if (cnt == null) {
            return null;
        }
        String content = StringUtils.highlightCode(cnt);
        LinkStates state = LinkStates.READ;
        StringBuilder sbHtml = new StringBuilder();
        StringBuilder sbText = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            switch (state) {
            case READ:
                if (StringUtils.endsWith(buffer, START_LINK)) {
                    String substr = buffer.substring(0, buffer.length()
                            - START_LINK.length());
                    sbHtml.append(substr);
                    sbText.append(substr);
                    buffer.setLength(0);
                    state = LinkStates.LINK;
                    break;
                }
                buffer.append(ch);
                break;
            case LINK:
                if (ch == '}') {
                    String[] str = processLink(buffer.toString());
                    sbHtml.append(str[0]);
                    sbText.append(str[1]);
                    buffer.setLength(0);
                    state = LinkStates.READ;
                    break;
                }
                buffer.append(ch);
                break;
            }
        }


        // append remaining
        sbHtml.append(buffer);
        sbText.append(buffer);

        String sbString = sbText.toString().replaceAll("<\\S*?>","");        

               Description description = new Description();
        description.longDescr = sbHtml.toString();
        if (alwaysGenerateShort) {
            description.hasShort = true;
            description.shortDescr = sbString.length() > DESCR_MAX_LENGTH ? new StringBuilder()
                    .append(sbString.substring(0, DESCR_MAX_LENGTH)).append(
                            "...").toString()
                    : sbString;
        } else {
            description.hasShort = sbString.length() > DESCR_MAX_LENGTH;
            description.shortDescr = description.hasShort ? new StringBuilder()
                    .append(sbString.substring(0, DESCR_MAX_LENGTH)).append(
                            "...").toString() : null;
        }
        return description;
    }  


    /**
     * Read params from list of param tags and add them to list of params Just
     * simplifies param processing for class, method and event
     *
     * @param paramTags
     *            tags
     * @param params
     *            target list of params
     */
    private void readParams(List<ParamTag> paramTags, List<Param> params) {
            for (ParamTag paramTag : paramTags) {
                    Param param = new Param();
                    param.name = paramTag.getParamName();
                    param.type = paramTag.getParamType();
                    Description descr = inlineLinks(paramTag.getParamDescription());
                    param.description = descr != null ? descr.longDescr : null;
                    param.optional = paramTag.isOptional();
                    params.add(param);
            }
    }


    private void injectCustomTags(Doc doc, Comment comment) {
        for (extdoc.jsdoc.schema.Tag customTag : context.getCustomTags()) {
            Tag tag = comment.tag('@' + customTag.getName());
            if (tag != null) {
                DocCustomTag t = new DocCustomTag();
                String title = customTag.getTitle();
                String format = customTag.getFormat();
                t.title = title;
                t.value = format != null ? MessageFormat.format(format, tag.text()) : tag.text();
                doc.customTags.add(t);
            }
        }
    }

   

    /**
     * Process class 
     * @param comment Comment
     */
    private void processClass(Comment comment){

        DocClass cls = new DocClass();
        
        ClassTag classTag = comment.tag("@class");
        Tag singletonTag = comment.tag("@singleton");
        ExtendsTag extendsTag = comment.tag("@extends");
        Tag constructorTag = comment.tag("@constructor");
        List<ParamTag> paramTags = comment.tags("@param");
        Tag namespaceTag = comment.tag("@namespace");

        cls.className = classTag.getClassName();
        boolean found = false;
        for (DocClass d : context.getClasses()) {
            if (d.className.equals(cls.className)) {
                context.setCurrentClass(d);
                cls = d;
                found = true;
                break;
            }
        }
        if (!found) {
            context.addDocClass(cls);
        }

        if (cls.packageName == null) {
              if (namespaceTag != null) {
                  cls.packageName = namespaceTag.text();
                  cls.shortClassName = StringUtils
                          .separateByLastDot(cls.className)[1];
              } else {
                  String[] str = StringUtils.separatePackage(cls.className);
                  cls.packageName = str[0];
                  cls.shortClassName = str[1];
              }
          }

        cls.definedIn.add(context.getCurrentFile().fileName);
        if (!cls.singleton) {
              cls.singleton = singletonTag != null;
        }
        if (cls.parentClass == null) {
            cls.parentClass = (extendsTag != null) ? extendsTag.getClassName() : null;
        }       

        // Skip private classes
        if (comment.hasTag("@private") || comment.hasTag("@ignore")) {
            cls.hide = true;
        }

        // process inline links after class added to context
       // DEFCT17
       if (!cls.hasConstructor) {
           cls.hasConstructor = constructorTag != null;
           if (constructorTag != null) {
               cls.constructorDescription = inlineLinks(constructorTag.text(),
                       true);
               readParams(paramTags, cls.params);
           }
       }

        if (cls.description == null) {
            String description = classTag.getClassDescription();
            if (description == null && extendsTag != null) {
                description = extendsTag.getClassDescription();
            }
            Description descr = inlineLinks(description);
            cls.description = descr != null ? descr.longDescr : null;
        }
        // Process cfg declared inside class definition
        // goes after global className set
        List<CfgTag> innerCfgs = comment.tags("@cfg");
        for (CfgTag innerCfg : innerCfgs) {
            DocCfg cfg = getDocCfg(innerCfg);
            context.addDocCfg(cfg);
        }

        injectCustomTags(cls, comment);
    }

    /**
     * Helper method to process cfg in separate comment and in class
     * definition
     * @return cfg
     */
    private DocCfg getDocCfg(CfgTag tag){
        DocCfg cfg = new DocCfg();
        cfg.name = tag.getCfgName();
        cfg.type = tag.getCfgType();
        cfg.description = inlineLinks(tag.getCfgDescription());
        cfg.optional = tag.isOptional();
        cfg.className = context.getCurrentClass().className;
        cfg.shortClassName =
                context.getCurrentClass().shortClassName;
        return cfg;
    }


    /**
     * Process cfg
     * @param comment Comment
     */
    private void processCfg(Comment comment){
        // Skip private
        if (comment.hasTag("@private")
                || comment.hasTag("@ignore")) return;
        CfgTag tag = comment.tag("@cfg");
        DocCfg cfg = getDocCfg(tag);
        cfg.hide = comment.tag("@hide")!=null;
        injectCustomTags(cfg, comment);
        context.addDocCfg(cfg);
    }

    /**
     * Process property 
     * @param comment Comment
     * @param extraLine first word form the line after comment
     */
    private void processProperty(Comment comment,String extraLine){
        // Skip private
        if (comment.hasTag("@private") || comment.hasTag("@ignore")) {
            return;
        }

        
        DocProperty property = new DocProperty();

        PropertyTag propertyTag = comment.tag("@property");
        TypeTag typeTag = comment.tag("@type");

        property.name = StringUtils.separateByLastDot(extraLine)[1];
        String description = comment.getDescription();
        if (propertyTag!=null){
            String propertyName = propertyTag.getPropertyName();
            if (propertyName!=null && propertyName.length()>0){
                property.name = propertyName;
            }
            String propertyDescription = propertyTag.getPropertyDescription();
            if (propertyDescription!=null && propertyDescription.length()>0){
                description = propertyDescription;
            }
        }
        property.type = typeTag!=null?typeTag.getType():DEFAULT_TYPE;
        property.description = inlineLinks(description);
        property.className = context.getCurrentClass().className;
        property.shortClassName = context.getCurrentClass().shortClassName;
        property.hide = comment.tag("@hide")!=null;
        injectCustomTags(property, comment);
        context.addDocProperty(property);
    }

    /**
     * Process method 
     * @param comment Comment
     * @param extraLine first word form the line after comment
     */
    private void processMethod(Comment comment, String extraLine){
        // Skip private
        if (comment.hasTag("@private") || comment.hasTag("@ignore")) {
            return;
        }

        DocMethod method = new DocMethod();

        Tag methodTag = comment.tag("@method");
        Tag staticTag = comment.tag("@static");
        List<ParamTag> paramTags = comment.tags("@param");
        ReturnTag returnTag = comment.tag("@return");
        MemberTag memberTag = comment.tag("@member");

        // should be first because @member may redefine class
        DocClass doc = context.getCurrentClass();
        method.className = doc!=null?doc.className:null;
        method.shortClassName = doc!=null?doc.shortClassName:null;
        method.name = StringUtils.separatePackage(extraLine)[1];
        if (methodTag!=null){
            if (!methodTag.text().isEmpty()){
                method.name = methodTag.text();
            }
        }
        if (memberTag!=null){
            String name = memberTag.getMethodName();
            if (name!=null){
                method.name = name;
            }
            method.className = memberTag.getClassName();
            method.shortClassName =
                    StringUtils.separatePackage(method.className)[1];
        }
        method.isStatic = (staticTag!=null);

        // renaming if static
//        if(method.isStatic){
//            method.name = new StringBuilder()
//                    .append(shortClassName)
//                    .append('.')
//                    .append(separateByLastDot(extraLine)[1])
//                    .toString();
//        }

        method.description = inlineLinks(comment.getDescription(), true);
        if (returnTag!=null){
            method.returnType =returnTag.getReturnType();
            method.returnDescription =returnTag.getReturnDescription();
        }
        readParams(paramTags, method.params);
        method.hide = comment.tag("@hide")!=null;
        injectCustomTags(method, comment);
        context.addDocMethod(method);
    }

    /**
     * Process event
     * @param comment Comment
     */
    private void processEvent(Comment comment){
        // Skip private
        if (comment.hasTag("@private")  || comment.hasTag("@ignore")) {
            return;
        }

        DocEvent event = new DocEvent();
        EventTag eventTag = comment.tag("@event");
        List<ParamTag> paramTags = comment.tags("@param");
        event.name = eventTag.getEventName();
        event.description = inlineLinks(eventTag.getEventDescription(), true);
        readParams(paramTags, event.params);
        event.className = context.getCurrentClass().className;
        event.shortClassName = context.getCurrentClass().shortClassName;
        event.hide = comment.tag("@hide")!=null;
        injectCustomTags(event, comment);
        context.addDocEvent(event);
    }

    enum CommentType{
        CLASS, CFG, PROPERTY, METHOD, EVENT
    }

    static CommentType resolveCommentType(Comment comment){
        return resolveCommentType(comment, "", "");
    }

    static CommentType resolveCommentType(Comment comment, String extraLine, String extra2Line){
        if(comment.hasTag("@class")){
            return CommentType.CLASS;
        }else if(comment.hasTag("@event")){
            return CommentType.EVENT;
        }else if(comment.hasTag("@cfg")){
            return CommentType.CFG;
        }else if(comment.hasTag("@param")
                || comment.hasTag("@return")
                || comment.hasTag("@method")){
            return CommentType.METHOD;
        }else if (comment.hasTag("@type")
                || comment.hasTag("@property")){
            return CommentType.PROPERTY;                    
        }else if(extra2Line.equals("function")){
            return CommentType.METHOD;
        }else{
            return CommentType.PROPERTY;
        }
    }


    /**
     *  Determine type of comment and process it
     * @param content text inside / ** and * /
     * @param extraLine first word form the line after comment 
     */
    private void processComment(String content, String extraLine, String extra2Line){
        if (content==null) return;
        Comment comment = new Comment(content);
        switch (resolveCommentType(comment, extraLine, extra2Line)){
            case CLASS:
                processClass(comment);
                break;
            case CFG:
                processCfg(comment);
                break;
            case PROPERTY:
                processProperty(comment, extraLine);
                break;
            case METHOD:
                processMethod(comment, extraLine);
                break;
            case EVENT:
                processEvent(comment);
                break;
        }
    }

    private enum State {CODE, COMMENT}
    private enum ExtraState {SKIP, SPACE, READ, SPACE2, READ2}

    private static final String START_COMMENT = "/**";
    private static final String END_COMMENT = "*/";


    /**
     * Checks if char is white space in terms of extra line of code after
     * comments
     * @param ch character
     * @return true if space or new line or * or / or ' etc...
     */
    private boolean isWhite(char ch){
        return !Character.isLetterOrDigit(ch) && ch!='.' && ch!='_';
    }
    /**
     * Processes one file with state machine
     *
     * @param fileName
     *            Source Code file name
     */
    private void processFile(String fileName) {
        try {
            File file = new File(new File(fileName).getAbsolutePath());
            context.setCurrentFile(file);
            context.position = 0;
            logger.fine(MessageFormat.format("Processing: {0}", context
                    .getCurrentFile().fileName));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader
                            (new FileInputStream(file), ENCODING));
            int numRead;
            State state = State.CODE;
            ExtraState extraState = ExtraState.SKIP;
            StringBuilder buffer = new StringBuilder();
            StringBuilder extraBuffer = new StringBuilder();
            StringBuilder extra2Buffer = new StringBuilder();
            String comment = null;
            char ch;
            while ((numRead = reader.read()) != -1) {
                context.position++;
                ch = (char) numRead;
                buffer.append(ch);
                switch (state) {
                case CODE:
                    switch (extraState) {
                    case SKIP:
                        break;
                    case SPACE:
                        if (isWhite(ch)) {
                            break;
                        }
                        extraState = ExtraState.READ;
                        /* fall through */
                    case READ:
                        if (isWhite(ch)) {
                            extraState = ExtraState.SPACE2;
                            break;
                        }
                        extraBuffer.append(ch);
                        break;
                    case SPACE2:
                        if (isWhite(ch)) {
                            break;
                        }
                        extraState = ExtraState.READ2;
                        /* fall through */
                    case READ2:
                        if (isWhite(ch)) {
                            extraState = ExtraState.SKIP;
                            break;
                         }
                         extra2Buffer.append(ch);
                         break;
                     }
                     if (StringUtils.endsWith(buffer, START_COMMENT)) {
                         if (comment != null) {
                             // comment is null before the first comment starts
                             // so we do not process it
                             processComment(comment, extraBuffer.toString(),
                                     extra2Buffer.toString());
                         }
                         context.lastCommentPosition = context.position - 2;
                         extraBuffer.setLength(0);
                         extra2Buffer.setLength(0);
                         buffer.setLength(0);
                         state = State.COMMENT;
                     }
                     break;
                 case COMMENT:
                     if (StringUtils.endsWith(buffer, END_COMMENT)) {
                         comment = buffer.substring(0, buffer.length()
                                 - END_COMMENT.length());
                         buffer.setLength(0);
                         state = State.CODE;
                         extraState = ExtraState.SPACE;
                     }
                     break;
                 }
             }
             processComment(comment, extraBuffer.toString(), extra2Buffer
                     .toString());
             reader.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    private void createClassHierarchy(){
        for(DocClass docClass: context.getClasses()){
            for(DocClass cls: context.getClasses()){
                if(docClass.className.equals(cls.parentClass)){
                    ClassDescr subClass = new ClassDescr();
                    subClass.className = cls.className;
                    subClass.shortClassName = cls.shortClassName;
                    docClass.subClasses.add(subClass);
                    cls.parent = docClass;
                }
            }
            for(DocCfg cfg: context.getCfgs()){
                if(docClass.className.equals(cfg.className)){
                    docClass.cfgs.add(cfg);
                }
            }
            for(DocProperty property: context.getProperties()){
                if(docClass.className.equals(property.className)){
                    docClass.properties.add(property);
                }
            }
            for(DocMethod method: context.getMethods()){
                if(docClass.className.equals(method.className)){
                    docClass.methods.add(method);
                }
            }
            for(DocEvent event: context.getEvents()){
                if(docClass.className.equals(event.className)){
                    docClass.events.add(event);
                }
            }
        }
    }

    private <T extends DocAttribute> boolean isOverridden(T doc, List<T> docs){
        if (doc.name == null || doc.name.isEmpty()) return false;
        for(DocAttribute attr:docs){
            String docName = StringUtils.separateByLastDot(doc.name)[1];
            String attrName = StringUtils.separateByLastDot(attr.name)[1];
            if (docName.equals(attrName)) return true;
        }
        return false;
    }

    private <T extends Doc> void removeHidden
                                                                                        (List<T> docs){
        for(ListIterator<T> it = docs.listIterator(); it.hasNext();){
            if (it.next().hide)
                it.remove();
        }
    }

    private <T extends DocAttribute> void addInherited (List<T> childDocs, List<T> parentDocs){
        for(T attr: parentDocs) {
            if (!isOverridden(attr, childDocs) && !attr.isStatic){
                childDocs.add(attr);
            }
        }
    }


    private void injectInherited(){
        for(DocClass cls: context.getClasses()){
            DocClass parent = cls.parent;
            while(parent!=null){
                ClassDescr superClass = new ClassDescr();
                superClass.className = parent.className;
                superClass.shortClassName = parent.shortClassName;
                cls.superClasses.add(superClass);
                if (parent.className.equals(COMPONENT_NAME)){
                    cls.component = true;
                }
                addInherited(cls.cfgs, parent.cfgs);
                addInherited(cls.properties, parent.properties);
                addInherited(cls.methods, parent.methods);
                addInherited(cls.events, parent.events);
                parent = parent.parent;
            }
            removeHidden(cls.cfgs);
            removeHidden(cls.properties);
            removeHidden(cls.methods);
            removeHidden(cls.events);

            // sorting
            Collections.sort(cls.cfgs);
            Collections.sort(cls.properties);
            Collections.sort(cls.methods);
            Collections.sort(cls.events);

            Collections.reverse(cls.superClasses);
            Collections.sort(cls.subClasses);

        }
        removeHidden(context.getClasses());
    }

    private void createPackageHierarchy(){
        for(DocClass cls: context.getClasses()){
            context.addClassToTree(cls);
        }
        context.sortTree();
    }

    private void showStatistics(){
        logger.fine("*** STATISTICS ***") ;
        for (Map.Entry<String, Integer> e : Comment.allTags.entrySet()){
            logger.fine(e.getKey() + ": " + e.getValue());
        }
    }

    private Pattern filePattern 
            = Pattern.compile(StringUtils.wildcardToRegex(DEFAULT_MATCH));
    private boolean skipHidden = DEFAULT_SKIPHIDDEN;

    private void processDir(String dirName){
        File file = new File(dirName);
        if (file.exists()){
            if (!(skipHidden && file.isHidden())){
                if (file.isDirectory()){
                    String[] children = file.list();
                    for(String child : children){
                        processDir(dirName+File.separator+child);
                    }
                }else{
                    if(filePattern.matcher(file.getName()).matches()){
                        processFile(dirName);
                    }
                }
            }
        }else{
            // file not exists
            logger.warning(
                    MessageFormat.format("File {0} not found", dirName));
        }
    }

    public void process(String fileName, String[] extraSrc){
        try {

            // process project file
            if(fileName!=null){
                File xmlFile = new File(new File(fileName).getAbsolutePath());
                FileInputStream fileInputStream = new FileInputStream(xmlFile);
                JAXBContext jaxbContext =
                        JAXBContext.newInstance("extdoc.jsdoc.schema");
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                extdoc.jsdoc.schema.Doc doc =
                        (extdoc.jsdoc.schema.Doc) unmarshaller.
                                unmarshal(fileInputStream);
                extdoc.jsdoc.schema.Tags tags = doc.getTags();
                if (tags!=null){
                    context.setCustomTags(doc.getTags().getTag());
                }
                extdoc.jsdoc.schema.Sources srcs = doc.getSources();
                if (srcs!=null){
                    List<extdoc.jsdoc.schema.Source> sources = srcs.getSource();
                    if(sources!=null){
                        for(extdoc.jsdoc.schema.Source src: sources){
                            String m = src.getMatch();
                            Boolean sh = src.isSkipHidden();
                            skipHidden = sh!=null?sh:DEFAULT_SKIPHIDDEN;
                            filePattern = Pattern.compile(
                                    StringUtils.wildcardToRegex(
                                            m!=null?m:DEFAULT_MATCH)); 
                            processDir(xmlFile.getParent()+
                                    File.separator+
                                    src.getSrc());
                        }
                    }
                }
                fileInputStream.close();
            }
            
            // process source files from command line
            if(extraSrc!=null){
                for(String src : extraSrc){
                    processDir(src);
                }
            }

            showStatistics();
            createClassHierarchy();
            injectInherited();
            createPackageHierarchy();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


     private void copyDirectory(File sourceLocation , File targetLocation)
        throws IOException {

        // skip hidden
        if (sourceLocation.isHidden()) return;
         
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (String child : children) {
                copyDirectory(new File(sourceLocation, child),
                        new File(targetLocation, child));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private static final String WRAPPER_CODE_MARKER =
            "###SOURCE###";

    private void readWrapper(String wrapper, StringBuilder prefix,
                             StringBuilder suffix){
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader
                            (new FileInputStream(wrapper), ENCODING));
            int numRead;
            while((numRead=reader.read())!=-1 &&
                    !StringUtils.endsWith(prefix, WRAPPER_CODE_MARKER)){
                prefix.append((char)numRead);
            }
            int len = prefix.length();
            prefix.delete(len-WRAPPER_CODE_MARKER.length(),len);
            suffix.append((char)numRead);
            while((numRead=reader.read())!=-1){
                suffix.append((char)numRead);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copySourceFiles(String targetDir, String wrapper) {
        new File(targetDir).mkdirs();
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        readWrapper(wrapper, prefix, suffix);
        for (DocFile docFile : context.getDocFiles()) {
            try {
                File dst = new File(new StringBuilder().append(targetDir)
                        .append(File.separator).append(docFile.targetFileName)
                        .toString());
                StringBuilder buffer = new StringBuilder();
               BufferedReader reader =
                    new BufferedReader(new InputStreamReader
                            (new FileInputStream(docFile.file), ENCODING));
                // current character
                int numRead;
                // position in file
                int position = 0;
                // current doc
                ListIterator<Doc> it = docFile.docs.listIterator();
                Doc doc = it.hasNext() ? it.next() : null;
                buffer.append(prefix);
                while ((numRead = reader.read()) != -1) {
                    position++;
                    char ch = (char) numRead;
                    if (doc != null && position == doc.positionInFile) {
                        buffer.append(MessageFormat.format(
                                "<div id=\"{0}\"></div>", doc.id));
                        doc = it.hasNext() ? it.next() : null;
                    }
                    buffer.append(ch);
                }
                buffer.append(suffix);
                Writer out =
                        new BufferedWriter(new OutputStreamWriter
                                (new FileOutputStream(dst), ENCODING));
                out.write(buffer.toString());
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void saveToFolder(String folderName, String templateFileName){
        new File(folderName).mkdirs();
        try {

            File templateFile =
                    new File(new File(templateFileName).getAbsolutePath());
            String templateFolder = templateFile.getParent();

            // Read template.xml
            JAXBContext jaxbTplContext =
                    JAXBContext.newInstance("extdoc.jsdoc.tplschema");
            Unmarshaller unmarshaller = jaxbTplContext.createUnmarshaller();
            Template template = (Template) unmarshaller.
                        unmarshal(new FileInputStream(templateFile));
            ClassTemplate classTemplate = template.getClassTemplate();
            String classTplFileName = new StringBuilder()
                    .append(templateFolder)
                    .append(File.separator)
                    .append(classTemplate.getTpl())
                    .toString();
            String classTplTargetDir = new StringBuilder()
                    .append(folderName)
                    .append(File.separator)
                    .append(classTemplate.getTargetDir())
                    .toString();
            TreeTemplate treeTemplate = template.getTreeTemplate();
            String treeTplFileName = new StringBuilder()
                    .append(templateFolder)
                    .append(File.separator)
                    .append(treeTemplate.getTpl())
                    .toString();
            String treeTplTargetFile = new StringBuilder()
                    .append(folderName)
                    .append(File.separator)
                    .append(treeTemplate.getTargetFile())
                    .toString();

            logger.info("*** COPY RESOURCES ***") ;
            new File(classTplTargetDir).mkdirs();

            // Copy resources
            Resources resources = template.getResources();

            List<Copy> dirs = resources.getCopy();

            for(Copy dir : dirs){
                String src = new StringBuilder()
                    .append(templateFolder)
                    .append(File.separator)
                    .append(dir.getSrc())
                    .toString();
                String dst = new StringBuilder()
                    .append(folderName)
                    .append(File.separator)
                    .append(dir.getDst())
                    .toString();
                copyDirectory(new File(src), new File(dst));
            }


            logger.info("*** COPY SOURCE FILES ***");
            String sourceTargetDir = new StringBuilder()
                    .append(folderName)
                    .append(File.separator)
                    .append(template.getSource().getTargetDir())
                    .toString();
             logger.info(MessageFormat.format("Target folder: {0}",
                     sourceTargetDir));
            String wrapperFile = templateFolder + File.separator +
                    template.getSource().getWrapper(); 
            copySourceFiles(sourceTargetDir, wrapperFile);



            // Marshall and transform classes
            JAXBContext jaxbContext =
                    JAXBContext.newInstance("extdoc.jsdoc.docs");
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(
                    Marshaller.JAXB_FORMATTED_OUTPUT,
                    true
            );
            DocumentBuilderFactory builderFactory =
                    DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);

            TransformerFactory factory = TransformerFactory.newInstance();
            Templates transformation = 
                    factory
                            .newTemplates (new StreamSource(classTplFileName)) ;
            Transformer transformer = transformation.newTransformer();

            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();

            logger.info("*** SAVING FILES ***") ;
            for(DocClass docClass: context.getClasses()){
                logger.fine("Saving: " + docClass.className);
                String targetFileName = new StringBuilder()
                        .append(classTplTargetDir)
                        .append(File.separator)
                        .append(docClass.className)
                        .append('.')
                        .append(OUT_FILE_EXTENSION)
                        .toString();
                Document doc = docBuilder.newDocument();
                marshaller.marshal(docClass, doc);
                if (GENERATE_DEBUG_XML){
                    marshaller.marshal(docClass, new File(targetFileName+"_"));
                }
                Result fileResult = new StreamResult(new File(targetFileName));
                transformer.transform(new DOMSource(doc), fileResult);
                transformer.reset();
            }

            // Marshall and transform tree
            JAXBContext jaxbTreeContext =
                    JAXBContext.newInstance("extdoc.jsdoc.tree");
            Marshaller treeMarshaller = jaxbTreeContext.createMarshaller();
            treeMarshaller.setProperty(
                    Marshaller.JAXB_FORMATTED_OUTPUT,
                    true
            );

            Templates treeTransformation =
                    factory.newTemplates (new StreamSource(treeTplFileName)) ;
            Transformer treeTransformer = treeTransformation.newTransformer();
            Document doc =  builderFactory.newDocumentBuilder().newDocument();
            treeMarshaller.marshal(context.getTree(), doc);
            if (GENERATE_DEBUG_XML){
                    treeMarshaller.
                            marshal(context.getTree(), new File(treeTplTargetFile+"_"));
            }
            Result fileResult = new StreamResult(new File(treeTplTargetFile));
            treeTransformer.transform(new DOMSource(doc), fileResult);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
