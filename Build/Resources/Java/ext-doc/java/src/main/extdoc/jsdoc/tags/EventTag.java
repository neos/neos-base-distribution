package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:45:14
 */
public interface EventTag extends Tag {
    String getEventName();

    String getEventDescription();
}
