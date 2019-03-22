package com.jiangtou.server.exception;

import com.jiangtou.server.pojo.base.BaseResult;
import com.jiangtou.server.pojo.base.ResultStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * 异常控制处理器
 * @author 陆迪
 */
@RestControllerAdvice
public class ExceptionAdvice {

    private final static Logger logger = LogManager.getLogger(ExceptionAdvice.class.getName());


    /**
     * 捕捉所有未登陆异常，返回401状态码
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnLoginException.class)
    public BaseResult<Object> handleUnLogin(UnLoginException e) {
        logger.error("无权访问(Unauthorized):\t\n", e);
        return new BaseResult<>(ResultStatus.UNLOGIN, e.getMessage());
    }

    /**
     * 捕捉所有Shiro异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ShiroException.class})
    public BaseResult handle403(ShiroException e) {
        logger.error("无权访问(Unauthorized):\t\n", e);
        return new BaseResult(ResultStatus.UNAUTHORIZED);
    }

    /**
     * 单独捕捉Shiro(UnauthorizedException)异常
     * 该异常为访问有权限管控的请求而该用户没有所需权限所抛出的异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedException.class)
    public  BaseResult handle403(UnauthorizedException e) {
        logger.error("无权访问(Unauthorized):当前Subject没有此请求所需权限:\t\n", e);
        return new BaseResult(ResultStatus.UNAUTHORIZED);
    }

    /**
     * 单独捕捉Shiro(UnauthenticatedException)异常
     * 该异常为以游客身份访问有权限管控的请求无法对匿名主体进行授权，而授权失败所抛出的异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthenticatedException.class)
    public BaseResult handle403(UnauthenticatedException e) {
        logger.error("无权访问(Unauthorized):当前Subject是匿名Subject，请先登录(This subject is anonymous.):\t\n", e);
        return new BaseResult(ResultStatus.UNAUTHORIZED);
    }


    /**
     * 捕捉校验异常(BindException)
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public  BaseResult<String> validException(BindException e) {
        logger.error("数据校验错误:\t\n", e);

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> result = this.getValidError(fieldErrors);

        return new BaseResult<>(ResultStatus.PARAMETER_ERROR, result.get("errorMsg").toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public BaseResult<Object> validException(MethodArgumentNotValidException e) {
        logger.error("数据校验错误:\t\n", e);

         List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
         Map<String, Object> result = this.getValidError(fieldErrors);
        return new BaseResult<>(ResultStatus.PARAMETER_ERROR.getCode(), result.get("errorMsg").toString(), result.get("errorList"));

    }

    /**
     * 捕捉校验异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResult<Object> constraintViolationException(ConstraintViolationException e) {
        logger.error("其它错误:\t\n", e);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String errorMsg = getErrorMsg(constraintViolations);

        return new BaseResult<>(ResultStatus.PARAMETER_ERROR, errorMsg);
    }



    /**
     * 捕捉自定义异常
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public BaseResult<String> handle(BadRequestException e) {
        logger.error("其它错误:\t\n", e);

        return new BaseResult<>(e.getCode(), e.getMessage());
    }


    /**
     * 捕捉404异常
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class, NotFoundException.class})
    public BaseResult<String> handle(NoHandlerFoundException e) {
        logger.error("404:\t\n", e);

        return new BaseResult<>(ResultStatus.NOT_FOUND, e.getMessage());
    }




    /**
     * 捕捉其他所有异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public BaseResult<String> globalException(Throwable e) {
        logger.error("其它错误:\t\n", e);
        return new BaseResult<>(ResultStatus.BAD_REQUEST, e.toString() + ": " + e.getMessage());
    }

    /**
     * 获取状态码
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 获取校验错误信息
     * @param fieldErrors
     * @return
     */
    private Map<String, Object> getValidError(List<FieldError> fieldErrors) {
        Map<String, Object> result = new HashMap<>(16);
        List<String> errorList = new ArrayList<>();
        StringBuffer errorMsg = new StringBuffer("校验异常(ValidException):");
        for (FieldError error : fieldErrors) {
            errorList.add(error.getField() + "-" + error.getDefaultMessage());
            errorMsg.append(error.getField()).append("-").append(error.getDefaultMessage()).append(".");
        }
        result.put("errorList", errorList);
        result.put("errorMsg", errorMsg);
        return result;
    }

    /**
     * 遍历错误信息
     * @param constraintViolationSet
     * @return
     */
    private String getErrorMsg(Set<ConstraintViolation<?>> constraintViolationSet) {
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation constraintViolation : constraintViolationSet) {
            errorMsg.append(constraintViolation.getMessageTemplate()).append(";");
        }

        return errorMsg.toString();
    }

}
