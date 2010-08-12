package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:43:16
 */
public interface CfgTag extends Tag{
    String getCfgName();

    String getCfgType();

    String getCfgDescription();

     public boolean isOptional();
    
}
