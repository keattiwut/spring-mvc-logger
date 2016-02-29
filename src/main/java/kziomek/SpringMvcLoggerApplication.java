package kziomek;

import kziomek.filter.MDCFilter;
import kziomek.filter.logging.MvcLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@SpringBootApplication
public class SpringMvcLoggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMvcLoggerApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean mdcFilterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		MDCFilter mdcFilter = new MDCFilter();
		registrationBean.setFilter(mdcFilter);
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean mvcLoggingFilterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(getMvcLoggingFilter());
		registrationBean.setOrder(2);
		return registrationBean;
	}


	private Filter getMvcLoggingFilter() {
		MvcLoggingFilter mvcLoggingFilter = new MvcLoggingFilter();
		mvcLoggingFilter.setIncludePayload(true);
		mvcLoggingFilter.setIncludeQueryString(true);
		mvcLoggingFilter.setIncludeHeaders(true);
		mvcLoggingFilter.setMaxPayloadLength(5120);
		return mvcLoggingFilter;
	}
}
