/**
 * 
 */
package com.rabbitmq.management.groovy

import com.google.gson.Gson

/**
 * @author felgutie
 *
 */
class ToJson {

	String toString(){
		new Gson().toJson(this)
	}
	
}
