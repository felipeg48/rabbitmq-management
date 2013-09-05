/**
 * 
 */
package com.rabbitmq.management.groovy

import groovy.lang.Closure

import java.util.HashMap
import java.util.Map

/**
 * @author felgutie
 *
 */
class RabbitMQConsoleDSL {

	static admin(Closure c){
		def clone = c.clone()
		clone.delegate = new RabbitMQConsoleDSL()
		clone.resolveStrategy = Closure.DELEGATE_ONLY
		clone()
	}
	
	/**
	 * Creates exchages, queues, bindings, users, nodes, etc based on the closure
	 * @param c Closure
	 * @return
	 */
	def create(Closure c){ 		
		Create.admin new HashMap(this.params),c
	}
	
	/**
	 * Deletes exchanges, queues, bindings, users, nodes, etc based on the closure 
	 * @param c Closure
	 * @return
	 */
	def delete(Closure c){ 
		Delete.admin new HashMap(this.params),c 
	}
	
	/**
	 * Display information based on the key/value
	 * <p>
	 * Usage:
	 * <p>
	 * <code> display info:'all' </code>
	 * <p>
	 * @param params Map based on the following keys:
	 * <ul>
	 * <li>info
	 * </ul>
	 * The possible values are for <code>info</code> key:
	 * <ul>
	 * <li> 'all' or 'overview' - Various random bits of information that describe the whole system.
	 * <li> 'nodes' - A list of nodes in the RabbitMQ cluster.
	 * <li> 'extensions' - A list of extensions to the management plugin.
	 * <li> 'definitions' - The server definitions - exchanges, queues, bindings, users, virtual hosts, permissions and parameters. Everything apart from messages 
	 * <li> 'connections' - A list of all open connections.
	 * <li> 'channels' - A list of all open channels.
	 * <li> 'exchanges' - A list of all exchanges.
	 * <li> 'queues' - A list of all queues.
	 * <li> 'bindings' - A list of all bindings.
	 * <li> 'vhosts' - A list of all vhosts.
	 * <li> 'users' - A list of all users.
	 * <li> 'whoami' - Details of the currently authenticated user.
	 * <li> 'permissions' - A list of all permissions for all users.
	 * <li> 'parameters' - A list of all parameters.
	 * <li> 'policies' - A list of all policies.
	 * </ul>
	 * @return String, json format
	 */
	def display(params){
		println get(params)
	}
	
	/**
	 * Sends a message to the Exchange specified. This action is for testing purpose.
	 * <p>
	 * Usage:
	 * <p>
	 * <code>
	 * send{
	 *		message body:"",headers:[:],options:''
	 *	}
	 * </code>	
	 * @param message body
	 * @return String, json format
	 */
	def send(Closure c){
		
	}
	
	def get(Closure c){
		
	}
	
	def get(params){
		def result
		if(params.info){
			this.params.info = params.info
			result = RabbitMQConsole.getInfo(new HashMap(this.params))
		}
		result
	}
	
	/**
	 * This action defines an alert when some of the keys changed, and it's trigger based on
	 * the events defined
	 * @param when
	 * @return String, json format
	 */
	def alert(Closure c){
		Alert.admin new HashMap(this.params),c 
	}
	
	Map params = [:]
	
	/**
	 * This is a map that contains the configuration options for RabbitMQ
	 * @param params Map, with the following keys:
	 * <ul>
	 * <li> vhost - the virtual host, if not provided '/' is the default.
	 * <li> server - the server name (Ex: locahost, 192.168.2.1), if not provided 'localhost is used as default.
	 * <li> port - the port, if not provided the port 15672 is taken by default 
	 * <li> user - the user, if not provided 'guest' is used by default
	 * <li> password - the user's password, if not provided 'guest' is used by default
	 * </ul>
	 * @return
	 */
	def options(params){
		if(params.user && params.password)
			RabbitMQConsole.authenticateWithUserAndPassword(params.use, params.password)
		this.params = params
	}
}

class Get{
	private Map options
	private Get(options){
		this.options = [:]
		this.options = options
	}
	
	static admin(options,Closure c){
		def clone = c.clone()
		clone.delegate = new Get(options)
		clone.resolveStrategy = Closure.DELEGATE_ONLY
		clone()
	}
	
	def message(params){
		this.options << params
		println this.options
	}
}

class Send{	
	private Map options
	private Send(options){
		this.options = [:]
		this.options = options
	}
	
	static admin(options,Closure c){
		def clone = c.clone()
		clone.delegate = new Send(options)
		clone.resolveStrategy = Closure.DELEGATE_ONLY
		clone()
	}
	
	def message(params){
		this.options << params
		println this.options
	}
}


class Create {
	private Map options
	private Create(options){
		this.options = [:]
		this.options = options
	}
	
	static admin(options,Closure c){			
		def clone = c.clone()
		clone.delegate = new Create(options)
		clone.resolveStrategy = Closure.DELEGATE_ONLY
		clone()
	}
	
	/**
	 * Creates a Exchange based on the parameterts
	 * @param params
	 * @return
	 */
	def exchange(params){	
		this.options << params	
		println this.options
		RabbitMQConsole.createExchange exchange:params.name,options:params.options,vhost:params.vhost,server:params.server
	}
	
	def queue(params){
		this.options << params
		println this.options
		
	}
}

class Delete {
	private Map options
	private Delete(options){
		this.options = [:]
		this.options = options
	}
	
	static admin(options,Closure c){		
		def clone = c.clone()
		clone.delegate = new Delete(options)
		clone.resolveStrategy = Closure.DELEGATE_ONLY
		clone()
	}
	
	/**
	 * Deletes a Exchange based on the name
	 * @param params
	 * @return
	 */
	def exchange(params){		
		this.options << params	
		println this.options
		RabbitMQConsole.deleteExchange exchange:params.name,vhost:params.vhost,server:params.server
	}
}

class Alert{
	private Map options
	private Alert(options){
		this.options = [:]
		this.options = options
	}
	
	static admin(options, Closure c){
		def clone = c.clone()
		clone.delegate = new Alert(options)
		clone.resolveStrategy = Closure.DELEGATE_ONLY
		clone()
	}
	
	Map params = [:]
	/**
	 * Setting options for the trigger like a cron
	 * @param params
	 * @return
	 */
	def options(params){
		this.params = params
		println this.params	
	}
	
	/**
	 * Triggers an event
	 * @param params
	 * @return
	 */
	def when(params){
		this.options << params
		//println this.options
		if(this.options.message){
			if(this.options.message(unack:100,memory:101)){
				println "ALERT!!"
				if(this.options.send){
					if(this.options.send instanceof Closure){
						def info = [:]
						this.options.send(info)
					}else
						println this.options.send
						
					this.options.send = null
				}
			}
		}
	}
	
}
