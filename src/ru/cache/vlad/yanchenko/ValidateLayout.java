package ru.cache.vlad.yanchenko;

import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checking validity of all the fields in the application.
 *
 * Created by v.yanchenko on 02.09.2016.
 */
public class ValidateLayout {

    // To perform a check if a filename, extension or path is to have any of these letters.
    private String pathSpecialCharacters = "[:<>|*/?]";
    private String fileSpecialCharacters = "[\\\\:<>|*/?]";

    Repository repository = Repository.getInstance();

    ValidateLayout() {
//        Repository.FILES_FOLDER = validatePath(Repository.FILES_FOLDER);
//        Repository.FILE_PREFIX = validateFilePrefix(Repository.FILE_PREFIX);
//        validateFileExtention();
    }

    String validatePath(String path) {
        Pattern p = Pattern.compile(pathSpecialCharacters);
        Matcher m = p.matcher(path);
        if (m.find()) { // Some special characters are present, thus ...
            // Logging such event.
            repository.getLogger().info("Files path \"" + path
                    + "\" has some special letters.");
            // Make a files path to be of some valid value, say a current time.
            path = new GregorianCalendar().getTime().toString() + "\\";
            repository.getLogger().info("it is set to default " + path);
        } else {
            // If filepath has no backslash at the end, add it.
            if (path.lastIndexOf('\\') != path.length() - 1) {
                path += "\\";
                System.out.println(path);
            }
        }
        return path;
    }


    String validateFilePrefix(String filePrefix) {
        Pattern p = Pattern.compile(pathSpecialCharacters);
        Matcher m = p.matcher(filePrefix);
        if (m.find()) { // Some special characters are present, thus ...
            // Logging such event.
            repository.getLogger().info("File prefix \"" + filePrefix
                    + "\" has some special letters.");
            // Make a folder named, some valid path, say a current time.
            filePrefix = "cache_file";
            repository.getLogger().info("It is set to default " + filePrefix);
        } else {
            repository.getLogger().info("File prefix is set to: " + "");
        }
        return filePrefix;
    }

    String validateFileExtention() {
        return "";
    }
}
