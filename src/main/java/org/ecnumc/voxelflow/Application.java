package org.ecnumc.voxelflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @author liudongyu
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.ecnumc.voxelflow")
@MapperScan(basePackages = "org.ecnumc.voxelflow.mapper")
@ImportResource("classpath:applicationContext.xml")
public class Application {
	/**
	 * Spring Boot 启动类
	 * @param args 启动参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
