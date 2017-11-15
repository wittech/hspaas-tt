package com.huashi.developer.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

//@ControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

//	@ExceptionHandler(value = Exception.class)
//	@ResponseBody
//	public CommonResponse defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
//		CommonResponse response = new CommonResponse();
//		response.setCode(CommonApiCode.COMMON_SERVER_EXCEPTION.getCode());
//		response.setMessage("请求处理异常");
//		logger.error("URL: {} 请求异常， 异常信息：{} ", req.getRequestURL().toString(), e.getMessage());
//		return response;
//	}

	// 比如404的异常就会被这个方法捕获
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView handle404Error(HttpServletRequest req, HttpServletResponse rsp, Exception e) throws Exception {
		return handleError(req, rsp, e, "error-front", HttpStatus.NOT_FOUND);
	}

	// 500的异常会被这个方法捕获
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleError(HttpServletRequest req, HttpServletResponse rsp, Exception e) throws Exception {
		return handleError(req, rsp, e, "error-front", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(GlobalExceptionHandler.class);
	}
}
