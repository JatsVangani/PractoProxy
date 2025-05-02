package com.practo.proxy.PractoProxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.practo.proxy", "com.practo.commons.security", "com.practo.commons.webutils"})
public class PractoProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PractoProxyApplication.class, args);
	}

}
