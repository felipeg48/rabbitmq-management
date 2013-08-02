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
			options vhost:'/',server:'172.16.196.138', protocol: 'https'
			display info:'overview'

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
		}
		
	}

}
