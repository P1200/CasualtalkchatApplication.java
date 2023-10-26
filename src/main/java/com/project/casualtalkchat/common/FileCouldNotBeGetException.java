package com.project.casualtalkchat.common;

import com.project.casualtalkchat.register_page.ServiceException;

public class FileCouldNotBeGetException extends ServiceException {
    public FileCouldNotBeGetException(String path) {
        super("File couldn't be get from: " + path + ".");
    }
}
