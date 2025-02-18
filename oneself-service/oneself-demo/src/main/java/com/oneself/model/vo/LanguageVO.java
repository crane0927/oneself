package com.oneself.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/2/17
 * packageName com.oneself.model.vo
 * className LanguagerVO
 * description
 * version 1.0
 */
@Data
public class LanguageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    private List<Language> languages;

    @Data
    public static class Language implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private String key;


    }
}
