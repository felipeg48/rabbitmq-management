/**
 * 
 */
package com.rabbitmq.management.groovy

import groovy.json.JsonSlurper

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.DeleteMethod
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.PutMethod
import org.apache.commons.httpclient.methods.StringRequestEntity

import com.google.gson.Gson

/**
 * Generic REST API for access the RabbitMQ console
 * @author felgutie
 *
 */
class RabbitMQConsole {
	private static def credentials = new UsernamePasswordCredentials("guest","guest")
	private static HttpClient client = new HttpClient()
	private static final String DEFAULT_ENCODE = "UTF-8"
	private static final String DEFAULT_VHOST = "/"
	private static final String DEFAULT_PROTOCOL = "http"
	private static final String SECURED_PROTOCOL = "https"
	private static final String DEFAULT_SERVER = "localhost"
	private static final Integer DEFAULT_PORT = 15672
	private static final String URL = "#protocol://#server:#port/api/#path"
	
	static authenticateWithUserAndPassword(username,password){
		credentials = new UsernamePasswordCredentials(username,password)
	}
	
	/**
	 * Gets the Overview of the current Server.
	 * @param params, a map with the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * </ul>
	 * @return RabbitMQInfo class
	 */
	static def getInfo(params=null){
		
		params = params?:[:]
		
		if(params.info){
			switch(params.info){
				case "all":
				params.path = "overview"
				break
				case "queues":
				case "exchanges":
				case "bindings":
				params.path = "${params.info}/#vhost"
				break				
				default:
				params.path = params.info
				break
			}
		}else{
			params.info = "all"
			params.path = "overview"
		}
		def url = getURL(params)
		if(params.debug)
			println url
		def get = new GetMethod(url)
		get.setRequestHeader "content-type","application/json"
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod get
		if(params.info == "all")
			new Gson().fromJson get.responseBodyAsString,RabbitMQInfo.class
		else
			get.responseBodyAsString
	}
	
	/**
	 * Gets the Exchanges from the provided Host
	 * @param params, a map with the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided the default will be '/'
	 * </ul>
	 * @return a map with the Keys:
	 * <ul>
	 * <li>list: which contains a List of Exchange objects
	 * <li>json: string json format
	 * </ul>
	 */
	static def getExchangeListAsData(params=null){
		params = params?:[:]
		params.path = "exchanges/#vhost"
		
		def get = new GetMethod(getURL(params))
		get.setRequestHeader "content-type","application/json"
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod get
		def json = get.responseBodyAsString
		def result = new JsonSlurper().parseText(json)
		def exchanges = []
		result.each {
			exchanges << new Exchange(name:it.name,
									 vhost:it.vhost,
								   durable:it.durable,
								      type:it.type,
								  internal:it.internal,
								autoDelete:it.auto_delete,
								 arguments:new ExchangeArguments(xMaxHops:it.'x-max-hops'))
		}
		[list:exchanges,json:json]
	}
	
	/**
	 * Create an exchange on the vhost provided
	 * @param params, a name based parameters with the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>exchange: the name of the exchange
	 * <li>options: the options of the exchange in a json format. Ex:  '{"type":"direct","durable":true}'
	 * </ul>
	 * @return String, the response of the server
	 */
	static def createExchange(params){
		
		params.path = "exchanges/#vhost/${URLEncoder.encode(params.exchange,DEFAULT_ENCODE)}"
		
		def put = new PutMethod(getURL(params))
		put.setRequestHeader "content-type","application/json"
		put.setRequestEntity(new StringRequestEntity(params.options))
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(put)
		put.getRequestHeaders()
	}
	
	/**
	 * Deletes the provided exchange name
	 * @param params, a name based parameters with the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>exchange: the name of the exchange
	 * </ul> 
	 * @return String, the response of the server
	 */
	static def deleteExchange(params){
		
		params.path = "exchanges/#vhost/${URLEncoder.encode(params.exchange,DEFAULT_ENCODE)}"
			
		def delete = new DeleteMethod(getURL(params))
		delete.setRequestHeader "content-type","application/json"
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(delete)
		delete.getRequestHeaders()
	}
	
	/**
	 * Creates a Queue
	 * @param params, a name based parameters with the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>queue: the name of the queue
	 * <li>options: the options of the queue in a json format. Ex:  '{"auto_delete":false,"durable":true,"arguments":[],"node":"fgutierrezcru@vmware.com"}'
	 * </ul>
	 * @return
	 */
	static def createQueue(params){
		params.path = "queues/#vhost/${URLEncoder.encode(params.queue,DEFAULT_ENCODE)}"
		
		def put = new PutMethod(getURL(params))
		put.setRequestHeader "content-type","application/json"
		put.setRequestEntity(new StringRequestEntity(params.options))
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(put)
		put.getRequestHeaders()	
	}
	
	/**
	 * Deletes the provided queue name
	 * @param params, a name based parameters with the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>queue: the name of the queue
	 * </ul> 
	 * @return
	 */
	static def deleteQueue(params){
		params.path = "queues/#vhost/${URLEncoder.encode(params.queue,DEFAULT_ENCODE)}"
		
		def delete = new DeleteMethod(getURL(params))
		delete.setRequestHeader "content-type","application/json"
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(delete)
		delete.getRequestHeaders()
	}
	
	/**
	 * Creates a binding between queues and exchanges
	 * @param params, a name based parameters with the following keys
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>queue: the name of the queue
	 * <li>exchange: the name of the exchange
	 * <li>options: the options for creating the binding. Ex: '{"routing_key":"my_routing_key","arguments":[]}'
	 * </ul>
	 * @return
	 */
	static def createBinding(params){		
		params.path = "bindings/#vhost/e/${URLEncoder.encode(params.exchange,DEFAULT_ENCODE)}/q/${URLEncoder.encode(params.queue,DEFAULT_ENCODE)}"
		
		def post = new PostMethod(getURL(params))
		post.setRequestHeader "content-type","application/json"
		post.setRequestEntity(new StringRequestEntity(params.options))
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(post)
		post.getRequestHeaders()
	}
	
	/**
	 * Deletes the Binding between a queue and exchanges
	 * @param params, a name based parameters with the following keys
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>queue: the name of the queue
	 * <li>exchange: the name of the exchange
	 * <li>options:  is a "name" for the binding composed of its routing key and a hash of its arguments.
	 * </ul>
	 * @return
	 */
	static def deleteBinding(params){
		params.path = "bindings/#vhost/e/${URLEncoder.encode(params.exchange,DEFAULT_ENCODE)}/q/${URLEncoder.encode(params.queue,DEFAULT_ENCODE)}/${URLEncoder.encode(params.options,DEFAULT_ENCODE)}"
		
		def delete = new DeleteMethod(getURL(params))
		delete.setRequestHeader "content-type","application/json"
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(delete)
		delete.getRequestHeaders()
	}
	
	/**
	 * Gets the node by its name and return status of it
	 * @param params, a name based parameters with the following keys
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>name: the node's name
	 * </ul>
	 * @return
	 */
	static def getNode(params){
		params.path = "nodes/${URLEncoder.encode(params.name,DEFAULT_ENCODE)}?memory=true"
		
		def get = new GetMethod(getURL(params))
		get.setRequestHeader "content-type","application/json"
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod get
		get.responseBodyAsString
	}
	
	/**
	 * Publish a message to the exchange
	 * @param params, a name based parameters with the following keys
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>exchange: the name of the exchange to publish the message to
	 * <li>options: the message and its properties. Ex. '{"properties":{},"routing_key":"my key","payload":"my body","payload_encoding":"string"}'
	 * </ul>
	 * @return
	 */
    static def publish(params){
		params.path = "exchanges/#vhost/${URLEncoder.encode(params.exchange,DEFAULT_ENCODE)}/publish"
		
		def post = new PostMethod(getURL(params))
		post.setRequestHeader "content-type","application/json"
		post.setRequestEntity(new StringRequestEntity(params.options))
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(post)
		post.getRequestHeaders()
	}
	
	/**
	 * Gets a message from the queue
	 * @param params, a name based parameters with the following keys
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided, the default will be '/'
	 * <li>queue: the name of the queue to get the message from
	 * <li>options: the different option to get the message. Ex. '{"count":5,"requeue":true,"encoding":"auto","truncate":50000}'
	 * </ul>
	 * @return
	 */
	static def getMessage(params){
		params.path = "queues/#vhost/${URLEncoder.encode(params.queue,DEFAULT_ENCODE)}/get"
		
		def post = new PostMethod(getURL(params))
		post.setRequestHeader "content-type","application/json"
		post.setRequestEntity(new StringRequestEntity(params.options))
		client.state.setCredentials AuthScope.ANY, credentials
		client.executeMethod(post)
		post.responseBodyAsString
	}
	
	/**
	 * Buils the final URL based on the params map
	 * @param params, is a map containing the following keys:
	 * <ul>
	 * <li>server: the server name/ip, if not one provided the default will be 'localhost'
	 * <li>port: the port number, if not one provided the default will be 15672
	 * <li>vhost: the vhost, if not one provided the default will be '/'
	 * <li>path: the api path (see the RabbitMQ reference)
	 * </ul>
	 * @return String, the final url. Ex. http://localhost:15672/api/exchanges
	 */
	private static String getURL(params){
		def server = DEFAULT_SERVER
		if(params.server)
			server = params.server
		def port = DEFAULT_PORT
		if(params.port)
			port = params.port 
		def protocol = DEFAULT_PROTOCOL
		if(params.protocol)
			if("ssl" == params.protocol.toLowerCase())
				protocol = SECURED_PROTOCOL
			else
				protocol = params.protocol
		
		def vhost = URLEncoder.encode(DEFAULT_VHOST,DEFAULT_ENCODE)
		if(params.vhost)
			vhost =  URLEncoder.encode(params.vhost,DEFAULT_ENCODE)
		
		URL.replace("#protocol", protocol)
		   .replace("#server", server)
		   .replace("#port",port.toString())
		   .replace("#path",params.path.replace("#vhost",vhost))
	}
}
