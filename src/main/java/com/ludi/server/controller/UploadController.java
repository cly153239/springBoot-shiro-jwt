package com.ludi.server.controller;

import com.ludi.server.enums.LogTypeEnum;
import com.ludi.server.exception.BadRequestException;
import com.ludi.server.pojo.base.BaseResult;
import com.ludi.server.pojo.base.ResultStatus;
import com.ludi.server.pojo.vo.FileInfoVo;
import com.ludi.server.service.file.UploadLogFileService;
import com.ludi.server.util.TimeUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @author 陆迪
 * @date 2019/3/15 15:40
 **/
@RequestMapping("/upload/")
@RestController
@Validated
public class UploadController {

    @Resource
    private UploadLogFileService uploadLogFileService;

    /**
     * @param deviceId
     * @param fileDateStr   日志日期
     * @param logType    日志类型
     * @param uploadFile
     * @return
     */
    @RequestMapping(value = "file/file-date/{fileDateStr}/device/{deviceId}", method = RequestMethod.POST)
    public BaseResult<FileInfoVo> uploadLogFile(
            @PathVariable @Pattern(regexp = "2019-[0-9]{2}-[0-9]{2}", message = "文件日期格式错误，正确格式'yyyy-MM-dd'") String fileDateStr,
            @PathVariable @Pattern(regexp = "[0-9a-zA-Z\\-]{10,}", message = "设备ID格式错误.") String deviceId,
            @RequestParam("logType") String logType,
            @RequestParam("uploadFile") MultipartFile uploadFile) throws BadRequestException {

        Date fileDate = TimeUtil.getDate(fileDateStr);
        if (fileDate == null) {
            throw new BadRequestException(ResultStatus.PARAMETER_ERROR, "日志日期格式错误.");
        }

        LogTypeEnum logTypeEnum;
        try {
            logTypeEnum = LogTypeEnum.getFileType(logType);
        } catch (Exception e) {
            throw new BadRequestException(ResultStatus.PARAMETER_ERROR, "日志类型错误，无法找到匹配的日志类型");
        }

        if (uploadFile.isEmpty()) {
            throw new BadRequestException(ResultStatus.PARAMETER_ERROR, "文件为空..");
        }

        FileInfoVo fileInfoVo;
        fileInfoVo = uploadLogFileService.saveUploadLogFile(fileDate, deviceId, logTypeEnum, uploadFile);

        return new BaseResult<>(ResultStatus.OK, fileInfoVo);
    }

}
