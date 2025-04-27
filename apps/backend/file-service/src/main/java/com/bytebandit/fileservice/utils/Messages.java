package com.bytebandit.fileservice.utils;

public final class Messages {
    public static final String USER_NOT_AUTHORIZED_TO_SHARE =
        "User is not authorized to share this item.";
    public static final String ITEM_SHARED_SUCCESSFULLY = "Item shared successfully.";
    public static final String ITEM_SHARED_WITH_PASSWORD =
        "Item shared successfully with password.";
    public static final String ITEM_SHARED_WITH_EXPIRES_AT =
        "Item shared successfully with expiration time.";
    public static final String ITEM_SHARED_WITH_PASSWORD_AND_EXPIRES_AT =
        "Item shared successfully with password and expiration time.";
    
    private Messages() {
        // Prevent instantiation
    }
}