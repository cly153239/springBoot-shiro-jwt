package com.jiangtou.server.service.file;

import com.jiangtou.server.enums.LogTypeEnum;
import com.jiangtou.server.exception.BadRequestException;
import com.jiangtou.server.pojo.vo.FileInfoVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author 陆迪
 * @date 2019/3/16 9:23
 * 日志文件上传服务类
 **/
@Service
public interface UploadLogFileService {

    /**
     * 返回保存文件的路径信息
     * @param fileDate
     * @param deviceId
     * @param logType
     * @param uploadFile
     * @return 上传文件在服务器的保存目录
     * @throws BadRequestException
     */
    FileInfoVo saveUploadLogFile(Date fileDate, String deviceId, LogTypeEnum logType, MultipartFile uploadFile) throws BadRequestException;

}
