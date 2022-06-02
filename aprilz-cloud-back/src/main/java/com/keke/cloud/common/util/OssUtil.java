package com.keke.cloud.common.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OSS工具包
 *
 * @author haoshuaiwei
 */
@Component
public class OssUtil {

    private OSS ossClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String uploadIdSymbol = "XX:FILE:UPLOAD:ID:";
    private String partETagsSymbol = "XX:FILE:PART:ETAGS:";

    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    @Value("${aliyun.endpoint}")
    private String endpoint ;
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId ;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret ;
    // 填写Bucket名称，例如examplebucket。
    @Value("${aliyun.bucketName}")
    private String bucketName = "apirlz";
    // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
   // String objectName = "exampledir/exampleobject.txt";

    @PostConstruct
    private void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);
    }

    /**
     * 上初始化大文件上传环境，返回uploadId
     *
     * @param fileName 文件
     * @return 返回uploadId
     */
    public String bigFileInitUpload(String fileName) {
        // 初始化分片上传
        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, fileName);

        // 如果需要在初始化分片时设置请求头，请参考以下示例代码。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // 指定该Object的网页缓存行为。
        // metadata.setCacheControl("no-cache");
        // 指定该Object被下载时的名称。
        // metadata.setContentDisposition("attachment;filename=oss_MultipartUpload.txt");
        // 指定该Object的内容编码格式。
        // metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
        // 指定初始化分片上传时是否覆盖同名Object。此处设置为true，表示禁止覆盖同名Object。
        // metadata.setHeader("x-oss-forbid-overwrite", "true");
        // 指定上传该Object的每个part时使用的服务器端加密方式。
        // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
        // 指定Object的加密算法。如果未指定此选项，表明Object使用AES256加密算法。
        // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_DATA_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
        // 指定KMS托管的用户主密钥。
        // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_KEY_ID, "9468da86-3509-4f8d-a61e-6eab1eac****");
        // 指定Object的存储类型。
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard);
        // 指定Object的对象标签，可同时设置多个标签。
        // metadata.setHeader(OSSHeaders.OSS_TAGGING, "a:1");
        // request.setObjectMetadata(metadata);

        // 初始化分片。
        InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识。您可以根据该uploadId发起相关的操作，例如取消分片上传、查询分片上传等。
        String uploadId = upresult.getUploadId();

        //将uploadId缓存到redis,1个小时有效
        redisTemplate.opsForValue().set(uploadIdSymbol + fileName , uploadId, 60 * 60 , TimeUnit.SECONDS);
        return uploadId;
    }

    /**
     * 上传指定的文件片断，返回uploadId
     *
     * @param fileName 分片初始化标识
     * @param chunkId 文件ID
     * @param file 分片文件流
     * @return 文件MD5
     *
     */
    public String uploadChunk(String fileName,
                              Integer chunkId,
                              MultipartFile file) throws IOException {
        InputStream inStream = file.getInputStream();
        long curPartSize = file.getSize();
        String uploadId = String.valueOf(redisTemplate.opsForValue().get(uploadIdSymbol + fileName));

        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(fileName);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setInputStream(inStream);
        // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
        uploadPartRequest.setPartSize(curPartSize);
        // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
        uploadPartRequest.setPartNumber(chunkId);
        // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
        // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
        PartETag partETag = uploadPartResult.getPartETag();
        //需要将PartETag缓存存起来，如果是第一个块，需要先将创建list，以便add
        Object partETagListStr = redisTemplate.opsForValue().get(partETagsSymbol + uploadId);
        List<PartETag> partETagList = new ArrayList<PartETag>();
        if(ObjectUtil.isNotEmpty(partETagListStr)){
            partETagList = JSONUtil.toList(JSONUtil.parseArray(partETagListStr), PartETag.class);
        }
        if (partETagList == null) {
            partETagList = new ArrayList<PartETag>();
        }
        partETagList.add(partETag);
        redisTemplate.opsForValue().set(partETagsSymbol + uploadId, JSONUtil.toJsonStr(partETagList), 60 * 60 * 5, TimeUnit.SECONDS);
        String md5Str = partETag.getETag();
        return md5Str;
    }

    /**
     * 合并文件，返回文件URL
     *
     * @param fileName 分片ID
     * @return 返回文件返回URL
     */
    public String completeFile(String fileName) throws JsonProcessingException {
        //获取该uploadId缓存的信息和各块的信息，
        String uploadId = String.valueOf( redisTemplate.opsForValue().get(uploadIdSymbol + fileName));
        String partETagListStr = String.valueOf( redisTemplate.opsForValue().get(partETagsSymbol + uploadId));
        List<PartETag> partETagList = JSONUtil.toList(JSONUtil.parseArray(partETagListStr), PartETag.class);
       // 创建CompleteMultipartUploadRequest对象。
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, fileName, uploadId, partETagList);

        // 如果需要在完成分片上传的同时设置文件访问权限，请参考以下示例代码。
        // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.Private);
        // 指定是否列举当前UploadId已上传的所有Part。如果通过服务端List分片数据来合并完整文件时，以上CompleteMultipartUploadRequest中的partETags可为null。
        // Map<String, String> headers = new HashMap<String, String>();
        // 如果指定了x-oss-complete-all:yes，则OSS会列举当前UploadId已上传的所有Part，然后按照PartNumber的序号排序并执行CompleteMultipartUpload操作。
        // 如果指定了x-oss-complete-all:yes，则不允许继续指定body，否则报错。
        // headers.put("x-oss-complete-all","yes");
        // completeMultipartUploadRequest.setHeaders(headers);

        // 完成合并，并返回结果
        CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);

        redisTemplate.delete(uploadIdSymbol + fileName);
        redisTemplate.delete(partETagsSymbol + uploadId);
        return completeMultipartUploadResult.getLocation();
    }



}
