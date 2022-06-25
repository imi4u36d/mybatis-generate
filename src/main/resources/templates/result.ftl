package ${packageUrl};

import ${dtoUrl}.BaseResponseDto;
import lombok.Data;

@Data
public class Result {

    private static final int okCode = 200;

    private static final int failCode = 500;

    private static final String okMsg = "请求成功";

    private static final String failMsg = "请求失败";


    public static <T> BaseResponseDto<T> success() {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(okCode);
        responseDto.setMsg(okMsg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> success(Integer code) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(code);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> success(String msg) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(okCode);
        responseDto.setMsg(msg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> success(T data) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(okCode);
        responseDto.setMsg(okMsg);
        responseDto.setData(data);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> success(Integer code, String msg) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(code);
        responseDto.setMsg(msg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> success(String msg, T data) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(okCode);
        responseDto.setMsg(msg);
        responseDto.setData(data);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> success(Integer code, String msg, T data) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(code);
        responseDto.setMsg(msg);
        responseDto.setData(data);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> fail() {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(failCode);
        responseDto.setMsg(failMsg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> fail(Integer code) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(code);
        responseDto.setMsg(failMsg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> fail(String msg) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(failCode);
        responseDto.setMsg(msg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> fail(Integer code, String msg) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(code);
        responseDto.setMsg(msg);
        return responseDto;
    }

    public static <T> BaseResponseDto<T> fail(Integer code, String msg, T data) {
        BaseResponseDto<T> responseDto = new BaseResponseDto<>();
        responseDto.setCode(code);
        responseDto.setMsg(msg);
        responseDto.setData(data);
        return responseDto;
    }
}
