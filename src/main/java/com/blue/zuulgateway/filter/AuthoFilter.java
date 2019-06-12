package com.blue.zuulgateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.jboss.logging.Logger;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限拦截器：采用header传token的形式，企业应用采用ACL
 */
@Component
public class AuthoFilter extends ZuulFilter {
	private static Logger log=Logger.getLogger(AuthoFilter.class);

	/**
	 * 业务逻辑
	 */
	public Object run() throws ZuulException {
		RequestContext context=RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String token = request.getHeader("token");
		if(StringUtils.isEmpty(token)){
			context.setSendZuulResponse(false);
			context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
			log.info("权限不足，已拦截");
		}
		return null;
	}

	/**
	 * 是否过滤
	 */
	public boolean shouldFilter() {
		RequestContext context=RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String uri = request.getRequestURI();
		log.info("Filter:AuthoFilter");
		if("/apigateway/order/order/saveOrder".equals(uri)){
			return true;
		}
		return false;
	}

	/**
	 * 值小先执行
	 */
	public int filterOrder() {
		return 0;
	}

	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

}
