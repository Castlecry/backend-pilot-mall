package com.pilot.common.exception;

import com.pilot.common.api.CommonResult;
import com.pilot.common.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception e) {
        log.error("系统异常", e);
        return CommonResult.failed(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public CommonResult<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return CommonResult.failed(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public CommonResult<String> handleValidException(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException) {
            FieldError fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
            message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        } else {
            FieldError fieldError = ((BindException) e).getBindingResult().getFieldError();
            message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        }
        log.error("参数校验异常: {}", message);
        return CommonResult.failed(ResultCode.VALIDATE_FAILED.getCode(), message);
    }
}
