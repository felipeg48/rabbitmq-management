/**
 *
 */
package com.rabbitmq.management.groovy

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * @author felgutie
 *
 */
class RabbitMQInfo extends ToJson{
	@SerializedName("management_version")
	String managementVersion
	@SerializedName("statistics_level")
	String statisticsLevel
	@SerializedName("exchange_types")
	Exchange[] exchangeTypes
	@SerializedName("rabbitmq_version")
	String rabbitmqVersion
	@SerializedName("erlang_version")
	String erlangVersion
	@SerializedName("message_stats")
	String[] messageStats
	@SerializedName("queue_totals")
	QueueTotal queueTotals
	@SerializedName("object_totals")
	ObjectTotals objectTotals
	String node
	@SerializedName("statistics_db_node")
	String statisticsDbNode
	Listener[] listeners
	Context[] contexts
	
}

/**
 * Exchange Information
 * @author felgutie
 *
 */
class Exchange{
  String name
  String description
  boolean enabled
  String vhost
  String type
  boolean durable
  @SerializedName("auto_delete")
  boolean autoDelete
  boolean internal
  ExchangeArguments arguments
  
}

class ExchangeArguments{
	@SerializedName("x-max-hops")
	Integer xMaxHops
}

class QueueTotal{

    String messages
	@SerializedName("messages_ready")
    Integer messagesReady
	@SerializedName("messages_unacknowledged")
    Integer messagesUnacknowledged
	@SerializedName("messages_details")
    MessageDetails messagesDetails
	@SerializedName("messages_ready_details")
    MessageReadyDetails messagesReadyDetails
	@SerializedName("messages_unacknowledged_details")
    MessageUnacknowledgedDetails messagesUnacknowledgedDetails
}

class MessageDetails{
    
    Integer rate
    Integer interval
	@SerializedName("last_event")
    Integer lastEvent

}

class MessageReadyDetails{
    
    Integer rate
    Integer interval
    @SerializedName("last_event")
    Integer lastEvent
}

class MessageUnacknowledgedDetails{
    Integer rate
    Integer interval
    @SerializedName("last_event")
    Integer lastEvent
}

class ObjectTotals{
    Integer consumers
    Integer queues
    Integer exchanges
    Integer connections
    Integer channels
}
 
class Listener{
      String node
      String protocol
	  @SerializedName("ip_address")
      String ipAddress
      Integer port
}

class Context{
    String node
    String description
    String path
    Integer port
	@SerializedName("ignore_in_use")
    boolean ignoreInUse
}
