package com.ludi.server.service.file.impl;

import com.ludi.server.enums.LogTypeEnum;
import com.ludi.server.exception.BadRequestException;
import com.ludi.server.pojo.base.ResultStatus;
import com.ludi.server.pojo.vo.FileInfoVo;
import com.ludi.server.service.file.UploadLogFileService;
import com.ludi.server.util.StringUtil;
import com.ludi.server.util.TimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author 陆迪
 * @date 2019/3/16 9:25
 **/
@Service
public class UploadLogFileServiceImpl implements UploadLogFileService {

    private final static Logger logger = LogManager.getLogger(UploadLogFileServiceImpl.class.getName());

    private final static String LOG_BASE_PATH = "C:\\ludi\\upload\\";

    @Override
    public FileInfoVo saveUploadLogFile(Date fileDate, String deviceId, LogTypeEnum logType, MultipartFile uploadFile) throws BadRequestException {

        String monthDateStr = TimeUtil.getMonthDate(fileDate);


        String filePathPrefix = LOG_BASE_PATH + monthDateStr + logType.value() + "\\" +
                deviceId + "\\" + TimeUtil.getMonthDate(fileDate) + "\\";

        String fileName = uploadFile.getOriginalFilename();
        if (StringUtil.isEmpty(fileName)) {
            throw new BadRequestException(ResultStatus.PARAMETER_ERROR, "获取文件名失败.");
        }

        String saveFileName = fileDate + "\\-" + fileName;

        String filePath = filePathPrefix + saveFileName;
        String backupFilePath = filePathPrefix + "old\\" + saveFileName;

        File file = new File(filePath);
        File backupFile = new File(backupFilePath);

        //移动旧文件备份文件
        if (backupFile.exists()) {
            boolean deleteSuccess = backupFile.delete();
            if (!deleteSuccess) {
                logger.error("删除旧的备份文件失败.filePath:" + backupFilePath);
            }
        }

        //移动上一次上传的日志文件转移到子目录old下
        if (file.exists()) {
            boolean renameSuccess =  file.renameTo(backupFile);
            if (!renameSuccess) {
                logger.error("移动上一次上传的日志文件失败。filePath:" + filePath + ",backupFilePath:" + backupFilePath);
            }
        } else {
            if (!file.getParentFile().exists()) {
                boolean mkdirsSuccess = file.getParentFile().mkdirs();
                if (!mkdirsSuccess) {
                    logger.error("创建日志保存文件上级目录失败。filePath:" + filePath);
                }
            }
        }


        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(uploadFile.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new BadRequestException(ResultStatus.PARAMETER_ERROR, "读取文件失败");
        }

        return new FileInfoVo(filePath);
    }
}
