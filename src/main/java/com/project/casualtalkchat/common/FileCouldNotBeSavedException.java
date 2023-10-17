package com.project.casualtalkchat.common;

import com.project.casualtalkchat.register_page.ServiceException;

public class FileCouldNotBeSavedException extends ServiceException {
    public FileCouldNotBeSavedException(String filePath) {
        super("File couldn't be saved in " + filePath + ".");
    }
}
