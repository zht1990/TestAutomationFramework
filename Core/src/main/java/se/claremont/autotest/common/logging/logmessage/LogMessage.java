package se.claremont.autotest.common.logging.logmessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordam on 2017-03-29.
 */
public class LogMessage {

    List<LogMessagePartType> logMessageParts = new ArrayList<>();

    public LogMessage(String logMessage) {
        substituteDataElements(logMessage);
    }

    public LogMessage(LogMessagePartType[] logMessageParts){
        for(LogMessagePartType logMessagePart : logMessageParts){
            this.logMessageParts.add(logMessagePart);
        }
    }

    public String toString(){
        String returnString = "";
        for(LogMessagePartType logMessagePart : logMessageParts){
            returnString += logMessagePart.toString();
        }
        return returnString;
    }

    public String toHtml(){
        String returnString = "";
        for(LogMessagePartType logMessagePart : logMessageParts){
            returnString += logMessagePart.toHtml();
        }
        return returnString;
    }

    public String toKnownErrorSuggestionString(){
        String returnString = "";
        for(LogMessagePartType logMessagePart : logMessageParts){
            returnString += logMessagePart.toKnownErrorSuggestionString();
        }
        return returnString;
    }

    public boolean equals(LogMessage logMessage){
        if(this.logMessageParts.equals(logMessage.logMessageParts))return true;
        return false;
    }

    public boolean isSimilarButWithDifferentData(LogMessage logMessage){
        if(logMessage == null) return false;
        if(logMessage.logMessageParts.size() != logMessageParts.size()) return false;
        for(int i = 0; i < logMessageParts.size(); i++){
            if(!logMessageParts.get(i).getClass().equals(logMessage.logMessageParts.get(i).getClass())) return false;
            if(logMessageParts.get(i).getClass().equals(DataValue.class))continue;
            if(!logMessageParts.get(i).toString().equals(logMessage.logMessageParts.get(i).toString())) return false;
        }
        return true;

        //Actually this currently probably could be substituted with
        // if(logMessage.toKnownErrorSuggestionString().equals(this.toKnownErrorSuggestionString()))return true;
        // return false
        // but that would prevent custom extensions with own implementations of LogMessage classes
    }

    private void substituteDataElements(String instring){
        StringBuilder sb = new StringBuilder();
        String[] parts = instring.split("'");
        if(instring.startsWith("'")){
            for(int i = 0; i < parts.length; i = i+2){
                logMessageParts.add(new DataValue(parts[i]));
                //sb.append("'<span class=\"data\">").append(parts[i]).append("</span>'");
                if(i+1 < parts.length){
                    logMessageParts.add(new TextLogMessagePart(parts[i+1]));
                    //sb.append(parts[i+1]);
                }
            }
        }else {
            for (int i = 0; i < parts.length; i = i+2){
                logMessageParts.add(new TextLogMessagePart(parts[i]));
                //sb.append(parts[i]);
                if(i+1 < parts.length){
                    logMessageParts.add(new DataValue(parts[i+1]));
                    //sb.append("'<span class=\"").append(enumMemberNameToLower(TestCaseLogReporterHtmlLogFile.HtmlLogStyleNames.DATA.toString())).append("\">").append(parts[i + 1]).append("</span>'");
                }
            }
        }
    }

}