package com.web.curation.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.web.curation.common.error.UnauthorizedException;
import com.web.curation.service.JwtService;

@Component
public class JwtInterceptor implements HandlerInterceptor{
	
	public static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
	
	private static final String HEADER_AUTH = "auth-token";

	@Autowired
	private JwtService jwtServiceImpl;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		final String token = request.getHeader(HEADER_AUTH);

		if(token != null && jwtServiceImpl.isUsable(token)){
			logger.info("토큰 사용 가능 : {}", token);
			return true;
		}else{
			logger.info("토큰 사용 불가능 : {}", token);
			throw new UnauthorizedException();
		}

	}
}
