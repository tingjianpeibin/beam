package com.ksh.beam.common.annotion;

import java.lang.annotation.*;

/**
 * 自定义注解  限流
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})    
@Retention(RetentionPolicy.RUNTIME)    
@Documented    
public  @interface ServiceLimit { 
	 String description()  default "";
}
