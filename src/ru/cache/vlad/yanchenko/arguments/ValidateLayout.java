package ru.cache.vlad.yanchenko.arguments;

import ru.cache.vlad.yanchenko.caches.DirectoryException;
import ru.cache.vlad.yanchenko.caches.FileExtensionException;
import ru.cache.vlad.yanchenko.caches.FilePrefixException;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checking validity of all the fields in the application.
 *
 * Created by v.yanchenko on 02.09.2016.
 */
public class ValidateLayout {

    // To perform a check if a path is to have any of these letters.
    private String pathSpecialCharacters = "[:<>|*/?]";
    // To perform a check if a filename, extension or have any of these letters.
    private String fileSpecialCharacters = "[\\\\:<>|*/?]";
    // Logging the operations.
    private Logger logger;

    public ValidateLayout(Logger logger) {
        this.logger = logger;
    }

    public String validatePath(String path) throws DirectoryException {
        Pattern p = Pattern.compile(pathSpecialCharacters);
        Matcher m = p.matcher(path);
        if (m.find()) { // Some special characters are present, thus ...
            // Throwing an exception.
            throw new DirectoryException("Folder path \"" + path
                    + "\" has some special letters.",logger);
//            // Make a files path to be of some valid value, say a current time.
//            path = new GregorianCalendar().getTime().toString() + "\\";
//            repository.getLogger().info("it is set to default " + path);
        } else {
            // If filepath has no backslash at the end, add it.
            if (path.lastIndexOf('\\') != path.length() - 1) {
                path += "\\";
                System.out.println(path);
            }
        }
        return path;
    }

    public String validateFilePrefix(String filePrefix) throws FilePrefixException {
        Pattern p = Pattern.compile(fileSpecialCharacters);
        Matcher m = p.matcher(filePrefix);
        if (m.find()) { // Some special characters are present, thus ...
            // Throwing an exception.
            throw new FilePrefixException("File prefix \"" + filePrefix
                    + "\" has some special letters.", logger);
            // Make a folder named, some valid path, say a current time.
//            filePrefix = "cache_file";
//            repository.getLogger().info("It is set to default " + filePrefix);
        } else {
            logger.info("File prefix is set to: " + filePrefix);
        }
        return filePrefix;
    }

    public String validateFileExtension(String fileExtension) throws FileExtensionException {
        Pattern p = Pattern.compile(fileSpecialCharacters);
        Matcher m = p.matcher(fileExtension);
        if (m.find()) { // Some special characters are present, thus ...
            // Throwing an exception.
            throw new FileExtensionException("File prefix \"" + fileExtension
                    + "\" has some special letters.", logger);
            // Make a folder named, some valid path, say a current time.
//            filePrefix = "cache_file";
//            repository.getLogger().info("It is set to default " + filePrefix);
        } else {
            logger.info("File prefix is set to: " + fileExtension);
        }
        return fileExtension;
    }

}
