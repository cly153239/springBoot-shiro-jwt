package com.jiangtou.server.controller;

import com.jiangtou.server.service.log.OperationLogService;
import com.jiangtou.server.service.plu.PluBcdService;
import com.jiangtou.server.pojo.base.BaseResult;
import com.jiangtou.server.pojo.po.OperationLogPo;
import com.jiangtou.server.pojo.vo.PluBcdVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;

/**
 * @author 陆迪
 * @date 2019-03-09 14:38
 **/
@RestController
@RequestMapping(value = "/plu")
@Validated
public class PluBcdController {

    @Resource
    private OperationLogService operationLogService;

    @Resource
    private PluBcdService pluBcdService;

    /**
     *
     * @param shpNo 门店编码
     * @param bcd 商品条码
     * @return PluBcdVo
     */
    @RequestMapping(value = "/shp/{shpNo}/bcd/{bcd}")
    public BaseResult<PluBcdVo> getPluBcd(
            @PathVariable @Pattern(regexp = "[0-9]{3}", message = "门店编码错误") String shpNo,
            @PathVariable @Pattern(regexp = "[0-9]{1,13}", message = "商品编码错误") String bcd) {

       PluBcdVo pluBcdVo = pluBcdService.getPluBcdVo(shpNo, bcd);

        operationLogService.insertOperationLog(new OperationLogPo());
        return new BaseResult<>(pluBcdVo);
    }

}
