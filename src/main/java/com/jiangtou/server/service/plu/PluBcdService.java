package com.jiangtou.server.service.plu;

import com.jiangtou.server.pojo.vo.PluBcdVo;

import org.springframework.stereotype.Service;


/**
 * @author 陆迪
 * @date 2019-03-09 15:24
 **/
@Service
public interface PluBcdService {

    /**
     *
     * @param shpNo
     * @param bcd
     * @return
     */
    PluBcdVo getPluBcdVo(String shpNo, String bcd);
}
