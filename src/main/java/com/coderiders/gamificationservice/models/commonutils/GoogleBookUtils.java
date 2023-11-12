package com.coderiders.gamificationservice.models.commonutils;


import com.coderiders.gamificationservice.models.commonutils.models.googleBooks.GoogleBook;
import com.coderiders.gamificationservice.models.commonutils.models.googleBooks.IndustryIdentifier;

public class GoogleBookUtils {

    /**
     * Retrieves the ISBN-10 identifier from a GoogleBook object.
     *
     * @param googleBook the GoogleBook object from which to extract the ISBN-10 identifier.
     * @return the ISBN-10 identifier as a String, or null if the identifier is not present or if
     * the industryIdentifiers field is null.
     */
    public static String getISBN10(GoogleBook googleBook) {
        if (googleBook.volumeInfo.industryIdentifiers == null) return null;

        return googleBook.volumeInfo.industryIdentifiers.stream()
                .filter(id -> id.getType().equalsIgnoreCase("ISBN_10"))
                .findFirst().map(IndustryIdentifier::getIdentifier)
                .orElse(null);
    }

    /**
     * Retrieves the ISBN-13 identifier from a GoogleBook object.
     *
     * @param googleBook the GoogleBook object from which to extract the ISBN-13 identifier.
     * @return the ISBN-13 identifier as a String, or null if the identifier is not present or if
     * the industryIdentifiers field is null.
     */
    public static String getISBN13(GoogleBook googleBook) {
        if (googleBook.volumeInfo.industryIdentifiers == null) return null;

        return googleBook.volumeInfo.industryIdentifiers.stream()
                .filter(id -> id.getType().equalsIgnoreCase("ISBN_13"))
                .findFirst().map(IndustryIdentifier::getIdentifier)
                .orElse(null);
    }

    /**
     * Converts the authors list of a GoogleBook object to a string array.
     *
     * @param googleBook the GoogleBook object from which to extract the authors.
     * @return a string array containing the authors, or an empty array if the authors list is null.
     */
    public static String[] getAuthors(GoogleBook googleBook) {
        return googleBook.volumeInfo.authors == null
                ? new String[0]
                : googleBook.volumeInfo.authors.toArray(new String[0]);
    }

    /**
     * Converts the categories list of a GoogleBook object to a string array.
     *
     * @param googleBook the GoogleBook object from which to extract the categories.
     * @return a string array containing the categories, or an empty array if the categories list is null.
     */
    public static String[] getCategories(GoogleBook googleBook) {
        return googleBook.volumeInfo.categories == null
                ? new String[0]
                : googleBook.volumeInfo.categories.toArray(new String[0]);
    }

    /**
     * Retrieves the small thumbnail link of a GoogleBook object.
     *
     * @param googleBook the GoogleBook object from which to extract the small thumbnail link.
     * @return the small thumbnail link as a string, or null if the imageLinks or smallThumbnail field is null.
     */
    public static String getSmallThumbnail(GoogleBook googleBook) {
        return googleBook.volumeInfo.imageLinks != null && googleBook.volumeInfo.imageLinks.smallThumbnail != null
                ? googleBook.volumeInfo.imageLinks.smallThumbnail
                : null;
    }

    /**
     * Retrieves the thumbnail link of a GoogleBook object.
     *
     * @param googleBook the GoogleBook object from which to extract the thumbnail link.
     * @return the thumbnail link as a string, or null if the imageLinks or thumbnail field is null.
     */
    public static String getThumbnail(GoogleBook googleBook) {
        return googleBook.volumeInfo.imageLinks != null && googleBook.volumeInfo.imageLinks.thumbnail != null
                ? googleBook.volumeInfo.imageLinks.thumbnail
                : null;
    }


}
