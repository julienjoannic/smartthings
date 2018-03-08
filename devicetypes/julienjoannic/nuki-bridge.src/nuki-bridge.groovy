/**
 *  Nuki Bridge
 *
 *  Copyright 2017 Julien JOANNIC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Nuki Bridge", namespace: "julienjoannic", author: "Julien JOANNIC") {
    	attribute "host", "string"
        attribute "id", "string"
        attribute "token", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    	multiAttributeTile(name: "bridge", type: "generic", width: 3, height: 2) {
        	tileAttribute("device.id", key: "PRIMARY_CONTROL") {
            	attributeState "default", label: '${currentValue}', icon: "https://nuki.io/wp-content/uploads/2016/04/nuki-product-bridge.png", defaultState: true
            }
            tileAttribute("device.host", key: "SECONDARY_CONTROL") {
            	attributeState "default", label: '${currentValue}', defaultState: true
            }
        }
        
        main("bridge")
	}
}

def updateLocks(locks) {
	log.debug "Updating locks of bridge ${device.currentValue("id")} from ${locks}"

	// Add new locks
	locks.each { lock ->
        if (lock.bridgeId == device.currentValue("id")) {
        	def lockDevice = getLockDevice(lock.lockId)
            if (!lockDevice && !state.lockIds?.contains(lock.lockId)) {
            	log.debug "Adding lock ${lock.nukiId} (${lock.name}) for bridge ${lock.bridgeId}"
                if (!state.lockIds) state.lockIds = []
                state.lockIds << lock.lockId
                addChildDevice("julienjoannic", "Nuki Lock", lock.lockId, null, ["isComponent": false, "label": lock.name])
       			lock.sendEvent(name: "lock", value: "unknown")
                childLockState(lock.lockId)
            }
        }
    }
    
    // Delete extra locks
    getChildDevices()?.each { device ->
    	log.debug "Checking if lock ${device.deviceNetworkId} is still selected (in ${locks})"
    	if (!locks.find { it.lockId == device.deviceNetworkId }) {
        	log.debug "Deleting lock ${device.deviceNetworkId}"
            state.lockIds.remove(lock.lockId)
        	deleteChildDevice(device.deviceNetworkId)
        }
    }
}

def childLockAction(dni, lockAction) {
    log.debug "Querying /lockAction ${lockAction} for ${dni}"
    
    def action = new physicalgraph.device.HubAction([
    	method: "GET",
    	path: "/lockAction",
        query: ["token": device.currentValue("token"),
        		"nukiId": dni.split(":")[1],
                "action": lockAction],
    	headers: [
        	HOST: device.currentValue("host")
    	]], dni, [callback: "onChildLockAction"]);
    
    pushRequest(action, ["dni": dni, "action": lockAction])
    sendHubCommand(action)
}

def onChildLockAction(physicalgraph.device.HubResponse response) {
    def values = popRequest(response)
	log.debug "Received /lockAction response for ${values.dni}: ${response}"
    log.debug response.properties
    
    if (response.json?.success) {
    	def lock = getChildDevices()?.find { it.deviceNetworkId == values.dni }
        if (lock) {
        	def value = [1: "unlocked", 2: "locked"][values.action]
       		lock.sendEvent(name: "lock", value: value)
        }
    }
}

def childLockState(dni) {
	log.debug "Querying http://${device.currentValue("host")}/lockState for ${dni}"
    
    def action = new physicalgraph.device.HubAction([
    	method: "GET",
    	path: "/lockState",
        query: ["token": device.currentValue("token"),
        		"nukiId": dni.split(":")[1]],
    	headers: [
        	HOST: device.currentValue("host")
    	]], dni, [callback: "onChildLockState"])
        
    pushRequest(action, dni)
    sendHubCommand(action)
}

def onChildLockState(physicalgraph.device.HubResponse response) {
	def dni = popRequest(response)
	log.debug "Received /lockState response: ${response.json} for ${dni}"
    
    if (response.json?.success) {
    	def lock = getLockDevice(dni)
        if (lock) {
        	def value = [1: "locked", 2: "unlocking", 3: "unlocked", 4: "locking", 255: "unknown"][response.json.state]
       		lock.sendEvent(name: "lock", value: value);
       }
    }
}

def getLockDevice(dni) {
	log.debug "Current devices of bridge ${device.currentValue("id")}"
	getChildDevices()?.each { lock ->
    	log.debug "${lock} (${lock.deviceNetworkId})"
    }

	def lock = getChildDevices()?.find { return it.deviceNetworkId.toString() == dni.toString() }
    if (!lock) {
       	log.warn "Could not find child device for DNI ${dni}"
    }
    
    return lock
}

def getCallbackUrl() {
	return "http://${device.hub.getDataValue("localIP")}:${device.hub.getDataValue("localSrvPortTCP")}/nuki"
}

def registerCallback() {
	log.debug "Querying http://${device.currentValue("host")}/callback/list"
    
    def action = new physicalgraph.device.HubAction([
    	method: "GET",
    	path: "/callback/list",
        query: ["token": device.currentValue("token")],
    	headers: [
        	HOST: device.currentValue("host")
    	]], dni, [callback: "onCallbackList"])
        
    sendHubCommand(action)
}

def onCallbackList(physicalgraph.device.HubResponse response) {
	log.debug "Received /callback/list response: ${response.json} (${response})"
    
    def url = getCallbackUrl()
    if (!response.json?.callbacks?.find { it.url == url }) {
        log.debug "Querying http://${device.currentValue("host")}/callback/add"

        def action = new physicalgraph.device.HubAction([
            method: "GET",
            path: "/callback/add",
            query: ["token": device.currentValue("token"),
            		url: url],
            headers: [
                HOST: device.currentValue("host")
            ]], dni, [callback: "onCallbackAdd"])

        sendHubCommand(action)
    }
}

def onCallbackAdd(physicalgraph.device.HubResponse response) {
	log.debug "Received /callback/add response: ${response.json}"
}

// parse events into attributes
def parse(String description) {
    def message = parseLanMessage(description)
    log.debug "Received message ${message.json}"
    if (message.json) {
        def dni = "${device.currentValue("id")}:${message.json.nukiId}"
        def lock = getLockDevice(dni)
        if (lock) {
            log.debug "Updating state of lock ${lock} to ${message.json.stateName}"
            lock.sendEvent(name: "lock", value: message.json.stateName)
        }
    }
}

def pushRequest(action, value) {   
    if (!state.requests) state.requests = [:]
    state.requests[action.requestId] = value
}

def popRequest(response) {
	def value = state.requests[response.requestId]
    state.requests.remove(response.requestId)
    
    return value
}