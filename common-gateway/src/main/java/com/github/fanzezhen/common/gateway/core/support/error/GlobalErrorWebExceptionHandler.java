package com.github.fanzezhen.common.gateway.core.support.error;

import com.github.fanzezhen.common.gateway.core.support.response.ActionResult;
import com.github.fanzezhen.common.gateway.core.support.response.ErrorInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * only print 500 server error, it's global
 * business related error will be handled by individual route
 * @author zezhen.fan
 */
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

	private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";

	private Logger logger = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);

	public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resourceProperties, applicationContext);
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	@NotNull
	private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		ActionResult<Void> result = new ActionResult<>();
		result.setSuccess(false);
		result.addError(serverError(request));
		return ServerResponse.status(HttpStatus.OK)
				.contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
				.body(BodyInserters.fromValue(result))
				.doFinally((e)-> logger.error("uncaught error {}",request.uri(),this.getError(request)))
				;
	}

	public ErrorInfo serverError(ServerRequest request){
		Object obj = request.exchange().getAttribute(ERROR_ATTRIBUTE);
		if(obj instanceof ResponseStatusException){
			ResponseStatusException responseStatusException = (ResponseStatusException) obj;
			return new ErrorInfo(responseStatusException.getStatus().value() +"",responseStatusException.getStatus().name());
		}
		return new ErrorInfo("500", "server error");
	}
}
