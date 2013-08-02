/**
 * 
 */
package com.rabbitmq.management.groovy

import static org.junit.Assert.*

import org.junit.Ignore
import org.junit.Test

import com.rabbitmq.management.groovy.RabbitMQConsole

/**
 * @author felgutie
 *
 */
class RabbitMQInfoTest {
	
	
	@Test
	@Ignore
	void info(){
		def info = RabbitMQConsole.info
		assertNotNull info
		println info
	}
	
	@Test
	@Ignore
	void listExchange(){
		def info = RabbitMQConsole.exchangeListAsData
		assertNotNull info
		println info
	}
	
	@Test
	@Ignore
	void createExchange(){
		def info = RabbitMQConsole.createExchange(exchange:"groovy-exchange",options:'{"type":"direct","durable":true}')
		assertNotNull info
		println info
	}
	
	@Test
	@Ignore
	void deleteExchange(){
		def info = RabbitMQConsole.deleteExchange(exchange:"groovy-exchange")
		assertNotNull info
		println info
	}
}
	
	