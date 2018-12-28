package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String url;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){

        try {
            //获取原名
            //file.getOriginalFilename();
            //读取配置文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //获取文件后缀名
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            //利用文件二进制上传文件
            //返回文件路径 便于用户取图片
            String path = fastDFSClient.uploadFile(file.getBytes(), ext);


            //上传成功将 url路径响应回去
            return new Result(true,url+path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
