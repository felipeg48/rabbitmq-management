package com.rabbitmq.management.groovy

import static org.junit.Assert.*

import org.junit.Test

import com.rabbitmq.management.groovy.RabbitMQConsoleDSL

class DSLTest {

	@Test
	void dsl(){
		
		//With or without (if without the certca.pem must be imported into the JAVA_HOME/jre/lib/security/cacerts file).
		//System.setProperty "javax.net.ssl.trustStore", "rabbitstore" 
		//System.setProperty "javax.net.ssl.trustStorePassword", ""
		
		RabbitMQConsoleDSL.admin {
			//options vhost:'/',server:'172.16.196.138', protocol: 'http'
			//display info:'overview'

			/*
			create {
				exchange name:'dsl-exchange',options:'{"type":"direct","durable":true}'
			}
			delete {
				exchange name:'dsl-exchange'
			}
			
			send{
				message body:"",headers:[:],options:''
			}
			
			get {
				message options:''
				
			}
			*/
			
			alert {
				options cron:"* * */?", emailto:"jdoe@critical.com"
				
				when message: { msg->
					msg.unack == 100 && msg.memory > 50
				}, send: "Error: UnAck and Memory"
				
				when message: { msg ->
					msg.memory > 100
				}, send: { info ->
					println "Critical, error in Memory"
					println info
				}
			}
		}
		
	}

}
