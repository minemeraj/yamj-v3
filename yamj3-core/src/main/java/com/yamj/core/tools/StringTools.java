package com.yamj.core.tools;

import com.yamj.common.tools.PropertyTools;
import java.io.File;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class StringTools {

    private static final Pattern CLEAN_STRING_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    // Number formatting
    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final long GB = KB * KB * KB;
    private static final DecimalFormat FILESIZE_FORMAT_0 = new DecimalFormat("0");
    private static final DecimalFormat FILESIZE_FORMAT_1 = new DecimalFormat("0.#");
    private static final DecimalFormat FILESIZE_FORMAT_2 = new DecimalFormat("0.##");
    private static final Map<Character, Character> charReplacementMap = new HashMap<Character, Character>();

    static {
        // Populate the charReplacementMap
        String temp = PropertyTools.getProperty("indexing.character.replacement", "");
        //String temp = PropertiesUtil.getProperty("mjb.charset.filename.translate", "");
        StringTokenizer tokenizer = new StringTokenizer(temp, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int idx = token.indexOf('-');
            if (idx > 0) {
                String key = token.substring(0, idx).trim();
                String value = token.substring(idx + 1).trim();
                if (key.length() == 1 && value.length() == 1) {
                    charReplacementMap.put(Character.valueOf(key.charAt(0)), Character.valueOf(value.charAt(0)));
                }
            }
        }
    }

    /**
     * Check the passed character against the replacement list.
     *
     * @param charToReplace
     * @return
     */
    public static String characterMapReplacement(Character charToReplace) {
        Character tempC = charReplacementMap.get(charToReplace);
        if (tempC == null) {
            return charToReplace.toString();
        } else {
            return tempC.toString();
        }
    }

    /**
     * Change all the characters in a string to the safe replacements
     *
     * @param stringToReplace
     * @return
     */
    public static String stringMapReplacement(String stringToReplace) {
        Character tempC;
        StringBuilder sb = new StringBuilder();

        for (Character c : stringToReplace.toCharArray()) {
            tempC = charReplacementMap.get(c);
            if (tempC == null) {
                sb.append(c);
            } else {
                sb.append(tempC);
            }
        }
        return sb.toString();
    }

    /**
     * Append a string to the end of a path ensuring that there are the correct number of File.separators
     *
     * @param basePath
     * @param additionalPath
     * @return
     */
    public static String appendToPath(String basePath, String additionalPath) {
        StringBuilder newPath = new StringBuilder(basePath.trim());
        newPath.append((basePath.trim().endsWith(File.separator) ? "" : File.separator));
        newPath.append(additionalPath.trim());
        return newPath.toString();
    }

    public static String cleanString(String sourceString) {
        return CLEAN_STRING_PATTERN.matcher(sourceString).replaceAll(" ").trim();
    }

    /**
     * Format the file size
     */
    public static String formatFileSize(long fileSize) {

        String returnSize;
        if (fileSize < KB) {
            returnSize = fileSize + " Bytes";
        } else {
            String appendText;
            long divider;

            // resolve text to append and divider
            if (fileSize < MB) {
                appendText = " KB";
                divider = KB;
            } else if (fileSize < GB) {
                appendText = " MB";
                divider = MB;
            } else {
                appendText = " GB";
                divider = GB;
            }

            // resolve decimal format
            DecimalFormat df;
            long checker = (fileSize / divider);
            if (checker < 10) {
                df = FILESIZE_FORMAT_2;
            } else if (checker < 100) {
                df = FILESIZE_FORMAT_1;
            } else {
                df = FILESIZE_FORMAT_0;
            }

            // build string
            returnSize = df.format((float) ((float) fileSize / (float) divider)) + appendText;
        }

        return returnSize;
    }

    /**
     * Check that the passed string is not longer than the required length and trim it if necessary.
     *
     * @param sourceString
     * @param requiredLength
     * @return
     */
    public static String trimToLength(String sourceString, int requiredLength) {
        return trimToLength(sourceString, requiredLength, Boolean.TRUE, "...");
    }

    /**
     * Check that the passed string is not longer than the required length and trim it if necessary
     *
     * @param sourceString The string to check
     * @param requiredLength The required length (Maximum)
     * @param trimToWord Trim the source string to the last space to avoid partial words
     * @param endingSuffix The ending to append if the string is longer than the required length
     * @return
     */
    public static String trimToLength(String sourceString, int requiredLength, boolean trimToWord, String endingSuffix) {
        String changedString = sourceString.trim();

        if (StringUtils.isNotBlank(changedString)) {
            if (changedString.length() <= requiredLength) {
                // No need to do anything
                return changedString;
            } else {
                if (trimToWord) {
                    BreakIterator bi = BreakIterator.getWordInstance();
                    bi.setText(changedString);
                    int biLength = bi.preceding(requiredLength - endingSuffix.length());
                    return changedString.substring(0, biLength).trim() + endingSuffix;
                } else {
                    // We know that the source string is longer that the required length, so trim it to size
                    return changedString.substring(0, requiredLength - endingSuffix.length()).trim() + endingSuffix;
                }
            }
        }

        return changedString;
    }

    /**
     * Cast a generic list to a specfic class See:
     * http://stackoverflow.com/questions/367626/how-do-i-fix-the-expression-of-type-list-needs-unchecked-conversion
     *
     * @param <T>
     * @param objClass
     * @param c
     * @return
     */
    public static <T> List<T> castList(Class<? extends T> objClass, Collection<?> c) {
        List<T> r = new ArrayList<T>(c.size());
        for (Object o : c) {
            r.add(objClass.cast(o));
        }
        return r;
    }

    /**
     * Split a list using a regex and return a list of trimmed strings
     *
     * @param stringToSplit
     * @param regexDelim
     * @return
     */
    public static List<String> splitList(String stringToSplit, String regexDelim) {
        List<String> finalValues = new ArrayList<String>();

        for (String output : stringToSplit.split(regexDelim)) {
            finalValues.add(output.trim());
        }

        return finalValues;
    }

    public static Collection<String> tokenize(String sourceString, String delimiter) {
        StringTokenizer st = new StringTokenizer(sourceString, delimiter);
        Collection<String> keywords = new HashSet<String>();
        while (st.hasMoreTokens()) {
            keywords.add(st.nextToken());
        }
        return keywords;
    }

    public static int toYear(String string) {
        if (StringUtils.isBlank(string)) {
            return -1;
        }
        if (!StringUtils.isNumeric(string)) {
            return -1;
        }

        try {
            int year = Integer.parseInt(string);
            if (year > 0) {
                return year;
            }
        } catch (Exception ignore) {
            // ignore this error
        }
        return -1;
    }
}