package com.keke.cloud.common.upload.factory;


import com.keke.cloud.common.domain.UploadFile;
import com.keke.cloud.common.upload.Uploader;
import com.keke.cloud.common.upload.product.ChunkUploader;

public class ChunkUploaderFactory implements UploaderFactory {

    @Override
    public Uploader getUploader() {
        return new ChunkUploader();
    }

    @Override
    public Uploader getUploader(UploadFile uploadFile) {
        return new ChunkUploader(uploadFile);
    }

}
