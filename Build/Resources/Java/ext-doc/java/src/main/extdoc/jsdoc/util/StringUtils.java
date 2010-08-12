package extdoc.jsdoc.util;

/**
 * User: Andrey Zubkov
 * Date: 06.12.2008
 * Time: 13:07:38
 */
public class StringUtils {

    public static class ClsAttrName{
        public String cls = "";
        public String attr = "";
        public String name = "";
    }

    enum LinkStates {CLS, ATTR, SKIPWHITE, NAME}
    /**
     * Processes link in format: [cls]#[attrib] [newName]
     * @param str Source String
     * @return Object containing class name, attribute (method, property,
     * etc.) and name (to be displayed)
     */
    public static ClsAttrName processLink(String str){
        ClsAttrName res  = new ClsAttrName();
        LinkStates state = LinkStates.CLS;
        int len = str.length(); 
        int start = 0;
        for(int i=0;i<len;i++){
            char ch = str.charAt(i);
            switch(state){
                case CLS:
                    if (ch == '#'){
                        res.cls = str.substring(start, i);
                        start = i+1;
                        state = LinkStates.ATTR;
                    }else if (Character.isWhitespace(ch)){
                        res.cls = str.substring(start, i);
                        start = i+1;
                        state = LinkStates.SKIPWHITE;
                    }
                    break;
                case ATTR:
                    if (!Character.isWhitespace(ch)) break;
                    res.attr = str.substring(start, i);
                    state = LinkStates.SKIPWHITE;                                            
                    // fall through
                case SKIPWHITE:
                    if (!Character.isWhitespace(ch)){
                        start = i;
                        state = LinkStates.NAME;                        
                    }
                    break;
            }
            if (state == LinkStates.NAME){
                res.name = str.substring(start, len);
                break;
            }
        }

        // process remaining
        switch(state){
            case CLS:
                res.cls = str.substring(start, len);
                break;
            case ATTR:
                res.attr = str.substring(start, len);
                break;            
        }

        return res;
    }

    /**
     * Highlights quotes with "em" tag
     * @param str input string
     * @return output string
     */
    static String highlightQuotes(String str){
        StringBuilder buffer = new StringBuilder();
        int len = str.length();
        boolean singleQuote = false;
        boolean doubleQuote = false;
        for(int i=0;i<len;i++){
            char ch = str.charAt(i);
            if (ch=='\''){
                buffer.append(singleQuote?"\'</em>":"<em>\'");
                singleQuote = !singleQuote;
            }else if (ch=='\"'){
                buffer.append(doubleQuote?"\"</em>":"<em>\"");
                doubleQuote = !doubleQuote;
            }else{
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    private static final String[] KEYWORDS = new String[]{
"break","continue","do","for","import","new","	this","void",
"case","default","else","function","in","return","typeof","while",
"comment","delete","export","if","label","switch","var","with"
    };

    private static boolean isKeyword(String word){
        for (String key : KEYWORDS) {
            if (key.equals(word)) return true;
        }
        return false;
    }

    /**
     * Highlights JavaScript keywords with "b" tag
     * @param str input string
     * @return output string
     */
    static String highlightKeywords(String str){
        StringBuilder buffer = new StringBuilder();
        int len = str.length();
        boolean isPrevAlpha = false;
        int last = 0;
        for(int i=0;i<len;i++){
            char ch = str.charAt(i);
            boolean isAlpha = Character.isLetter(ch);
            if (isAlpha != isPrevAlpha){
                String word = str.substring(last, i);
                buffer.append((!isAlpha && isKeyword(word))?"<b>"+word+"</b>":word);
                last = i;
            }
            isPrevAlpha = isAlpha;
        }
        // remaining
        String word = str.substring(last, len);
        buffer.append((isPrevAlpha && isKeyword(word))?"<b>"+word+"</b>":word);
        return buffer.toString();
    }

    /**
     * Highlights one ine comments with "i" tag
     * @param str input string
     * @return output string
     */
    static String highlightComments(String str){
        StringBuilder buffer = new StringBuilder();
        int len = str.length();
        boolean comment = false;
        for(int i=0;i<len;i++){
            char ch = str.charAt(i);
            if(!comment && ch=='/' && i<len-1 && str.charAt(i+1)=='/'){
                buffer.append("<i>");
                comment = true;
            }
            if (comment && ch=='\n'){
                buffer.append("</i>");
                comment = false;
            }
            buffer.append(ch);
        }
        // remaining
        if (comment){
            buffer.append("</i>");           
        }
        return buffer.toString();
    }


    /**
     * Highlights keywords, comments and quotes in the input string
     * @param str input string
     * @return output string
     */
    public static String highlight(String str){
        return highlightQuotes(
                        highlightKeywords(
                            highlightComments(str)));
    }

    public static class TokenHandler{
        protected String handle(String content){
            return content;
        }
    }

    public static String processTokens(String input,
                                       String startToken,
                                       String endToken,
                                       TokenHandler handler){
        StringBuilder buffer = new StringBuilder();
        int len = input.length();
        int slen = startToken.length();
        int elen = endToken.length();
        int last = 0;
        boolean token = false;
        for(int i=0;i<len;i++){
            if (!token && i>=slen && input.substring(i-slen, i).equals(startToken)){
                buffer.append(input.substring(last, i));
                last = i;
                token = true;                
            }else if (token && i>=elen && input.substring(i-elen, i).equals(endToken)){
                buffer.append(handler.handle(input.substring(last, i-elen)));
                buffer.append(endToken);
                last = i;
                token = false;
            }
        }
        // remaining
        String remain = input.substring(last, len);          
        buffer.append(token?handler.handle(remain):remain);
        return buffer.toString();        
    }

    /**
     * Highlights areas inside "code"
     * @param str input string
     * @return output string
     */
    public static String highlightCode(String str){
        return processTokens(str, "<code>", "</code>", new TokenHandler(){
            protected String handle(String content){
                return highlight(content.trim());                
            }
        });              
    }

    /**
     * Checks if StringBuilder ends with string
     */
    public static boolean endsWith(StringBuilder sb, String str){
        int len = sb.length();
        int strLen = str.length();
        return (len>=strLen && sb.substring(len-strLen).equals(str));
    }

     /**
     * Separates fullclass name to package and class
     * By this rule:
     * Ext => pkg: "" cls: "Ext"
     * Ext.Button => pkg: "Ext" cls: "Button"
     * Ext.util.Observable => pkg: "Ext.util" cls: "Observable"
     * Ext.layout.BorderLayout.Region => pkg: "Ext.layout" cls: "BorderLayout.Region"
     * Ext.Updater.BasicRenderer => pkg: "Ext" cls: "Updater.BasicRenderer"
     * @param className Class name to parse
     * @return Array of strings [0] package [1] class
     */
    public static String[] separatePackage(String className){
        String [] str = new String[2];
        String[] items = className.split("\\.");
        if (items.length == 1){
            str[0] = "";
            str[1] = className;
        }else{
            StringBuilder pkg = new StringBuilder(items[0]);
            StringBuilder cls = new StringBuilder(items[items.length - 1]);
            for(int i=items.length-2;i>0;i--){
                if (Character.isUpperCase(items[i].charAt(0))){
                    // if starts with capital it is a part of class name
                    cls.insert(0, '.');
                    cls.insert(0, items[i]);
                }else{
                    // insert remaining package name
                    for(int j =1;j<=i;j++){
                        pkg.append('.');
                        pkg.append(items[j]);
                    }
                    break;
                }
            }
            str[0] = pkg.toString();
            str[1] = cls.toString();
        }
        return str;
    }

    public static String[] separateByLastDot(String className){
        String[] str = new String[2];
        if(className==null) return str;
        int len = className.length();
        int i = len-1;
        while(i>=0 && className.charAt(i)!='.') i--;
        str[0] = (i>0)?className.substring(0,i):"";
        str[1] = className.substring(i+1,len);
        return str;
    }
    
    public static String wildcardToRegex(String wildcard){
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return(s.toString());
    }


}
