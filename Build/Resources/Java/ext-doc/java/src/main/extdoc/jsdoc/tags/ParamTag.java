package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:46:31
 */
public interface ParamTag extends Tag {
    String getParamType();

    String getParamName();

    String getParamDescription();

    boolean isOptional();
}
