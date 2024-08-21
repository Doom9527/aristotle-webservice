package com.paiondata.aristotle.exception;

import com.paiondata.aristotle.common.base.HttpStatus;
import com.paiondata.aristotle.common.base.R;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.paiondata.aristotle.exception.customize.CustomizeReturnException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 请求方式不支持
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestUri, e.getMethod());
        return R.fail(HttpStatus.BAD_METHOD, e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public R<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), Objects.isNull(e.getRequiredType()) ? "None" : e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * 拦截未知的运行时异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * 系统异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * Validation校验注解校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(ValidationException.class)
    public R<Void> handleValidationException(ValidationException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("校验失败'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Validation校验注解校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("校验失败'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * 数据重复异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public R<Void> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("数据重复'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * 方法参数校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("方法校验失败'{}',发生系统异常.", requestUri, e);
        String message = Objects.isNull(e.getBindingResult().getFieldError()) ? "No Message" : e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 自定义验证异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("自定义验证失败'{}',发生系统异常.", requestUri, e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 自定义未找到资源/路径或未开启服务异常
     */
    @ExceptionHandler(ServletException.class)
    public R<Void> handleServletException(ServletException e, HttpServletRequest request) {
        Throwable cause = e.getCause();
        String requestUri = request.getRequestURI();
        if (cause instanceof NoClassDefFoundError || cause instanceof ExceptionInInitializerError) {
            log.error("未启用服务'{}',发生系统异常.", requestUri, e);
            return R.fail(HttpStatus.ERROR, "未启用服务");
        }
        if (e instanceof NoHandlerFoundException || e instanceof NoResourceFoundException) {
            log.error("未找到路径或资源'{}',发生系统异常.", requestUri, e);
            return R.fail(HttpStatus.NOT_FOUND, "未找到路径或资源");
        }
        log.error("请求地址'{}',发生未知异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * 自定义返回异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeReturnException.class)
    public R<String> handleCustomizeReturnException(CustomizeReturnException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

}