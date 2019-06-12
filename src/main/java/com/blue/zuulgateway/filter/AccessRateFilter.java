package com.blue.zuulgateway.filter;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 限制指定URI访问高并发的访问量
 */
@Component
public class AccessRateFilter extends ZuulFilter {
	private static Logger log=Logger.getLogger(AccessRateFilter.class);
	
	/**
	 * 并发访问限流器：
	 */
	private static RateLimiter RATE_LIMITER=RateLimiter.create(100);

	public boolean shouldFilter() {
		RequestContext context=RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		String uri = request.getRequestURI();
		log.info("Filter:AccessRateFilter");
		if("/apigateway/order/order/saveOrder".equals(uri)){
			return true;
		}
		return false;
	}

	public Object run() throws ZuulException {
		RequestContext context=RequestContext.getCurrentContext();
		if(!RATE_LIMITER.tryAcquire()){
			context.setSendZuulResponse(false);
			context.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
			log.info("流量过大,请重试");
		}
		return null;
	}

	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	public int filterOrder() {
		return -5;
	}

}
