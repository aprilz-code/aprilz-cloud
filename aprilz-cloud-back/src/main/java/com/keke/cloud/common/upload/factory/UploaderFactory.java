package com.keke.cloud.common.upload.factory;


import com.keke.cloud.common.domain.UploadFile;
import com.keke.cloud.common.upload.Uploader;

public interface UploaderFactory {
    public Uploader getUploader();
    public Uploader getUploader(UploadFile uploadFile);
}
