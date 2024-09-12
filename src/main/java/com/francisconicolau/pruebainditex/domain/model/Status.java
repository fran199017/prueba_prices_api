package com.francisconicolau.pruebainditex.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Status implements Serializable {

    @Serial
    private static final long serialVersionUID = 2990294328594147647L;
    private Integer code;
    private String message;
    private String from;

    public Status(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.from = "DB";
    }

    public Status(Integer code, String message, String from) {
        this.code = code;
        this.message = message;
        this.from = from;
    }

    public static Status getSuccessRedisStatus() {
        return new Status(0, "Success", "Redis");
    }

    public static Status getSuccessDBStatus() {
        return new Status(0, "Success", "DB");
    }

}
