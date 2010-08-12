package extdoc.jsdoc.tags.impl;

import extdoc.jsdoc.tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Andrey Zubkov
 * Date: 30.10.2008
 * Time: 21:41:31
 */

public class Comment {

    public static Map<String, Integer> allTags
            = new HashMap<String, Integer>();

    private final List<Tag> tagList = new ArrayList<Tag>();

    private String description;

    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag>  T tag(String tagName){
        for(Tag tag: tagList){
            if (tag.name().equals(tagName)) return (T)tag;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> List<T> tags(String tagName){
        List<T> found = new ArrayList<T>();
        for(Tag tag : tagList){
            if (tag.name().equals(tagName)){
                found.add((T) tag);
            }
        }
        return found;
    }

    public boolean hasTag(String tagName){
        for(Tag tag: tagList){
            if (tag.name().equals(tagName)) return true;
        }
        return false;
    }

    private enum CommentState {SPACE, DESCRIPTION}

    private enum InnerState {TAG_NAME, TAG_GAP, IN_TEXT}

    /**
     * Constructor of Comment
     * @param content Comment
     */
    public Comment(final String content){

        class CommentStringParser{

            private boolean isStarWhite(char ch){
                  return Character.isWhitespace(ch) || ch=='*';
            }

            private String removeStars(){
                   CommentState state = CommentState.SPACE;
                    StringBuilder buffer = new StringBuilder();
                    StringBuilder spaceBuffer = new StringBuilder();
                    boolean foundStar = false;
                    for (int i=0;i<content.length();i++){
                        char ch = content.charAt(i);
                        switch (state){
                            case SPACE:
                                if (isStarWhite(ch)){
                                    if (ch == '*'){
                                        foundStar = true;
                                    }
                                    spaceBuffer.append(ch);
                                    break;
                                }
                                if (!foundStar){
                                    buffer.append(spaceBuffer);
                                }
                                spaceBuffer.setLength(0);
                                state = CommentState.DESCRIPTION;
                                /* fall through */
                            case DESCRIPTION:
                                if (ch == '\n'){
                                    foundStar = false;
                                    state = CommentState.SPACE;
                                }
                                buffer.append(ch);
                                break;
                        }
                    }
                    return buffer.toString();                
            }

            /**
             * Processes inner comment text
             * Very similar to Sun's com.sun.tools.javadoc#Comment             
             */
            void parseCommentStateMachine(){                    
                    String inner = removeStars();
                    InnerState instate = InnerState.TAG_GAP;
                    String tagName = null;
                    int tagStart =0;
                    int textStart =0;
                    boolean validTag = true;
                    int lastNonWhite = -1;
                    int len = inner.length();
                    for(int i=0;i<len;i++){
                        char ch = inner.charAt(i);
                        boolean isWhite = Character.isWhitespace(ch);
                        switch (instate){
                            case TAG_NAME:
                                if (isWhite){
                                    tagName = inner.substring(tagStart, i);
                                    instate = InnerState.TAG_GAP;
                                }
                                break;
                            case TAG_GAP:
                                if (isWhite){
                                    break;
                                }
                                textStart = i;
                                instate = InnerState.IN_TEXT;
                                /* fall through */
                            case IN_TEXT:
                                if (validTag && ch == '@'){
                                    parseCommentComponent(inner, tagName,
                                            textStart,lastNonWhite+1);
                                    tagStart = i;
                                    instate = InnerState.TAG_NAME;
                                }
                                break;
                        }                        
                        validTag = isWhite;
                        if(!isWhite){
                            lastNonWhite = i;                            
                        }
                    }
                    // Finish for last item
                    switch(instate){
                        case TAG_NAME:
                            tagName = inner.substring(tagStart, len);
                            /* fall through */
                        case TAG_GAP:
                            textStart = len;
                        case IN_TEXT:
                            parseCommentComponent(inner, tagName, textStart,
                                    lastNonWhite+1);
                            break;
                    }
            }

             private void parseCommentComponent(String content,
                                                    String tagName, int from, int upto) {
                String tx = upto <= from ? "": content.substring(from, upto);
                if (tagName == null){
                    description = tx;
                }else{
                    TagImpl tag;
                    if (tagName.equals("@class")){
                        tag = new ClassTagImpl(tagName, tx);
                    }else if (tagName.equals("@param")){
                        tag = new ParamTagImpl(tagName, tx);
                    }else if (tagName.equals("@extends")){
                        tag = new ExtendsTagImpl(tagName, tx);
                    }else if (tagName.equals("@cfg")){
                        tag = new CfgTagImpl(tagName, tx);
                    }else if (tagName.equals("@type")){
                        tag = new TypeTagImpl(tagName, tx);
                    }else if (tagName.equals("@return")){
                        tag = new ReturnTagImpl(tagName, tx);
                    }else if (tagName.equals("@member")){
                        tag = new MemberTagImpl(tagName, tx);
                    }else if (tagName.equals("@event")){
                        tag = new EventTagImpl(tagName, tx);
                    }else if (tagName.equals("@property")){
                        tag = new PropertyTagImpl(tagName, tx);
                    }else{
                        tag = new TagImpl(tagName, tx);
                    }

                    Integer num = allTags.get(tagName);
                    allTags.put(tagName, num==null?1:num+1);

                    tagList.add(tag);
                }
            }
            
        }
        new CommentStringParser().parseCommentStateMachine();

    }

}
