package com.oneself.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.vo
 * className DemoVO
 * description
 * version 1.0
 */
@Data
public class DemoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String info;

    public DemoVO() {
    }

    public DemoVO(String info) {
        this.info = info;
    }
}
