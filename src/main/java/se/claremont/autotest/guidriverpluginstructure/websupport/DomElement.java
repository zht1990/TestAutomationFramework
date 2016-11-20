package se.claremont.autotest.guidriverpluginstructure.websupport;

import se.claremont.autotest.guidriverpluginstructure.GuiElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Object declaration mechanisms for web elements
 *
 * Created by jordam on 2016-08-17.
 */
public class DomElement implements GuiElement {

    @SuppressWarnings("WeakerAccess")
    public final String name;
    private final String page;
    public final List<String> recognitionStrings;
    public final IdentificationType identificationType;
    public Integer ordinalNumber = null;

    /**
     * Identification mechanisms
     */
    public enum IdentificationType{
        BY_LINK_TEXT,
        BY_X_PATH,
        BY_ID,
        BY_CLASS,
        BY_CSS,
        BY_NAME,
        BY_VISIBLE_TEXT
    }

    /**
     * Declares a DOM element to be used in test execution
     * @param recognitionString the recognition string that identifies the object
     * @param identificationType what mechanism to use for identification
     */
    public DomElement (String recognitionString, IdentificationType identificationType){
        this.recognitionStrings = new ArrayList<>();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement callingMethodUsingConstructor = stackTraceElements[2];
        name = callingMethodUsingConstructor.getMethodName();
        page = callingMethodUsingConstructor.getClassName();
        this.recognitionStrings.add(recognitionString);
        this.identificationType = identificationType;
    }

    /**
     * Constructor for use for example with several languages
     *
     * @param alternativeRecognitionStrings An array of recognition strings for this element
     * @param identificationType The method of identification
     */
    public DomElement(String[] alternativeRecognitionStrings, IdentificationType identificationType){
        this.recognitionStrings = new ArrayList<>();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement callingMethodUsingConstructor = stackTraceElements[2];
        name = callingMethodUsingConstructor.getMethodName();
        page = callingMethodUsingConstructor.getClassName();
        for(String recognitionString : alternativeRecognitionStrings){
            this.recognitionStrings.add(recognitionString);
        }
        this.identificationType = identificationType;
    }

    /**
     * Declares a DOM element to be used in test execution
     * @param recognitionString the recognition string that identifies the object
     * @param identificationType what mechanism to use for identification
     * @param ordinalNumber The ordinal number of the occurance on the web page, if multiple matches for search criteria.
     */
    public DomElement (String recognitionString, IdentificationType identificationType, Integer ordinalNumber){
        this.recognitionStrings = new ArrayList<>();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement callingMethodUsingConstructor = stackTraceElements[2];
        name = callingMethodUsingConstructor.getMethodName();
        page = callingMethodUsingConstructor.getClassName();
        this.recognitionStrings.add(recognitionString);
        this.ordinalNumber = ordinalNumber;
        this.identificationType = identificationType;
    }

    /**
     * Constructor for use for example with several languages
     *
     * @param alternativeRecognitionStrings An array of recognition strings for this element
     * @param identificationType The method of identification
     * @param ordinalNumber The ordinal number of the occurance on the web page, if multiple matches for search criteria.
     */
    public DomElement(String[] alternativeRecognitionStrings, IdentificationType identificationType, Integer ordinalNumber){
        this.recognitionStrings = new ArrayList<>();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement callingMethodUsingConstructor = stackTraceElements[2];
        name = callingMethodUsingConstructor.getMethodName();
        this.ordinalNumber = ordinalNumber;
        page = callingMethodUsingConstructor.getClassName();
        for(String recognitionString : alternativeRecognitionStrings){
            this.recognitionStrings.add(recognitionString);
        }
        this.identificationType = identificationType;
    }

    /**
     * Enables unified logging formats for element references in the testCaseLog
     * @return a string to use in testCaseLog posts
     */
    public String LogIdentification(){
        return name + " (declared in page class " + page + ")";
    }


}
