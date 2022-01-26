package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author :Administrator
 * @description :TODO
 * @date :2022/1/22 14:41
 */
@Data
public class MessageStruct implements Serializable {

    private static final long serialVersionUID = 392365881428311040L;

    private String message;

    public MessageStruct(String message) {
        this.message = message;
    }
}
