package com.ksh.beam.system.controller.common;

import com.ksh.beam.common.util.OSSFactory;
import com.ksh.beam.common.utils.R;
import com.ksh.beam.common.utils.ToolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Api(value = "FileController", tags = {"文件上传接口"})
@RestController
@RequestMapping("/file")
public class FileController {

    @ApiOperation(value = "对象存储文件上传")
    @PostMapping("/upload")
    public Object uploadFile(@RequestPart("file") MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "." + ToolUtil.getFileSuffix(file.getOriginalFilename());
        try {
            String url = OSSFactory.build().upload(file.getBytes(), fileName);
            return R.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("上传图片失败");
        }
    }
}