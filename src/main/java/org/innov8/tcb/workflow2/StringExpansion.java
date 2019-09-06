package org.innov8.tcb.workflow2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StringExpansion
{
    private static final Pattern QAPattern= Pattern.compile("\\{[aq]:[\\w\\[\\]]+}");
    private static final Pattern StaticPattern = Pattern.compile("\\$\\{d:\\w+.[\\w{}:]+}");

    public static String expandLine(String line, Map<String, Object> metadata) {
        Matcher matcher = StaticPattern.matcher(line);
        if (matcher.find()) {
            String strToBeReplaced = matcher.group();
            String tmpStr = expandQA(strToBeReplaced, metadata);
            String newLine = line.replace(strToBeReplaced, tmpStr);
            Matcher newMatcher = StaticPattern.matcher(newLine);
            if (newMatcher.find())
            {
                strToBeReplaced = newMatcher.group();

                Object replaceData;
                if ((replaceData = metadata.get(strToBeReplaced.substring(2,
                                                                          strToBeReplaced.length() - 1))) == null)
                {
                    log.warn("Cannot find the variable: {}.", strToBeReplaced);
                    replaceData = "{nd:".concat(strToBeReplaced.substring(3));
                }
                return expandLine(newLine.replace(strToBeReplaced, (String)replaceData), metadata);
            }
            return newLine;
        }
        return expandQA(line, metadata);
    }

    private static String expandQA(String line, Map<String, Object> metadata) {
        Matcher matcher = QAPattern.matcher(line);
        if (matcher.find()) {
            String strToBeReplaced = matcher.group();
            Object replaceData;
            if ((replaceData = metadata.get(strToBeReplaced.substring(1,
                                                                     strToBeReplaced.length()-1))) == null) {
                log.warn("Cannot find the variable: {}.", strToBeReplaced);
                replaceData = "{n:".concat(strToBeReplaced.substring(3));
            }
            String newLine = line.replace(strToBeReplaced, (String)replaceData);
            return expandQA(newLine, metadata);
        }
        return line;
    }
}
