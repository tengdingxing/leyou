package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.controller.UploadController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {


        private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
        // 支持的文件类型

        //推荐使用fdfs上传，支持缩略图
        @Autowired
        private FastFileStorageClient storageClient;

        private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg","image/jpg");
        public String upload(MultipartFile file) {
            String originalFilename = file.getOriginalFilename();
            try {
                // 1、图片信息校验
                // 1)校验文件类型
                String type = file.getContentType();
                if (!suffixes.contains(type)) {
                    logger.info("上传失败，文件类型不匹配：{}", type);
                    return null;
                }
                // 2)校验图片内容
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    logger.info("上传失败，文件内容不符合要求");
                    return null;
                }
                // 2、保存图片

               // file.transferTo(new File("D:\\upload\\image\\" + originalFilename));
                String ext = StringUtils.substringBeforeLast(file.getOriginalFilename(), ".");
             StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

                // 2.3、返回url，进行回显
               // String url = "http://image.leyou.com/" +originalFilename;

                System.out.println("图片url"+ "http://image.leyou.com/"+storePath.getFullPath());
                return  "http://image.leyou.com/"+storePath.getFullPath();
            } catch (Exception e) {
                return null;
            }
        }


    }

