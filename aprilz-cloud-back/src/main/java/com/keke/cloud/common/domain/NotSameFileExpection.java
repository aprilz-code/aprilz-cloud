package com.keke.cloud.common.domain;

public class NotSameFileExpection extends Exception {
    public NotSameFileExpection() {
        super("File MD5 Different");
    }
}
