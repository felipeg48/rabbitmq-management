rabbitmq-management
===================
[![Build Status](https://drone.io/github.com/felipeg48/rabbitmq-management/status.png)](https://drone.io/github.com/felipeg48/rabbitmq-management/latest)

A RabbitMQ Management Groovy DSL

```groovy
		//With or without (if without the certca.pem must be imported into the JAVA_HOME/jre/lib/security/cacerts file).
		//System.setProperty "javax.net.ssl.trustStore", "rabbitstore"
		//System.setProperty "javax.net.ssl.trustStorePassword", ""

		RabbitMQConsoleDSL.admin {

			options vhost:'/',server:'172.16.196.138', protocol: 'http'
			display info:'overview'


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

				//IDEAS for the DSL on the 'alert' action
				when queue:{ q ->
					q.name == "myqueue"
					export plot > "/tmp/"

				}

			}
		}

```