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
        attribute "mac", "string"
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

def authenticate() {
	if (state.busy) return
    state.busy = true
    
	log.debug "Querying http://${device.currentValue("host")}/auth"
    
    sendHubCommand(new physicalgraph.device.HubAction([
    	method: "GET",
    	path: "/auth",
    	headers: [
        	HOST: device.currentValue("host")
    	]], deviceNetworkId, [callback: "onAuthentication"]))
}

def onAuthentication(physicalgraph.device.HubResponse response) {
	log.debug "Received /auth response: ${response.json}"
    
    if (response.json?.success) {
    	sendEvent(name: "token", value: response.json.token)
        sendEvent(name: "mac", value: response.mac.toString())
        log.debug "Updating bridge network ID to ${response.mac}"
        device.setDeviceNetworkId(response.mac.toString())
    }
    else {
    	sendEvent(name: "token", value: null)
    }
    state.busy = false
}

def authenticated() {
	return device.currentValue("token") != null
}

def list() {
	if (state.busy) return
	state.busy = true
    
	log.debug "Querying http://${device.currentValue("host")}/list"
    
    sendHubCommand(new physicalgraph.device.HubAction([
    	method: "GET",
    	path: "/list",
        query: ["token": device.currentValue("token")],
    	headers: [
        	HOST: device.currentValue("host")
    	]], deviceNetworkId, [callback: "onList"]))
}

def onList(physicalgraph.device.HubResponse response) {
	log.debug "Received /list response: ${response.json}"
    
    state.locks = response.json
    state.busy = false
}

def updateLocks(lockIds) {
	log.debug "Updating locks of bridge ${device.deviceNetworkId} from ${lockIds}"

	// Add new locks
	lockIds.each { lockId ->
    	def parts = lockId.split(":")
    	def bridgeId = parts[0]
        def nukiId = parts[1] as Integer
        if (bridgeId == device.deviceNetworkId) {
        	def device = getChildDevices()?.find { it.deviceNetworkId == lockId }
            if (!device) {
            	log.debug "Finding lock ${nukiId} in ${state.locks}"
            	def lock = state.locks.find { log.debug "'${it.nukiId}' == '${nukiId}' -> ${it.nukiId == nukiId}"; it.nukiId == nukiId }
            	log.debug "Adding lock ${nukiId} (${lock?.name}) for bridge ${bridgeId}"
            	device = addChildDevice("julienjoannic", "Nuki Lock", lockId, null, ["isComponent": false, "label": lock.name])
            }
        }
    }
    
    // Delete extra locks
    getChildDevices()?.each { device ->
    	log.debug "Checking if lock ${device.deviceNetworkId} is still selected (in ${lockIds})"
    	if (!lockIds.contains(device.deviceNetworkId)) {
        	log.debug "Deleting lock ${device.deviceNetworkId}"
        	deleteChildDevice(device.deviceNetworkId)
        }
    }
}

def childLockAction(dni, lockAction) {
    log.debug "Querying /lockAction for ${dni}"
    
    def action = new physicalgraph.device.HubAction([
    	method: "GET",
    	path: "/lockAction",
        query: ["token": device.currentValue("token"),
        		"nukiId": dni.split(":")[1],
                "lockAction": lockAction],
    	headers: [
        	HOST: device.currentValue("host")
    	]], dni, [callback: "onChildLockAction"]);
    
    pushRequest(action, ["dni": dni, "action": lockAction])
    sendHubCommand(action)
}

def onChildLockAction(physicalgraph.device.HubResponse response) {
    def values = popRequest(response)
	log.debug "Received /lockAction response for ${values.dni}: ${response}"
    
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
	log.debug "Received /lockState response: ${response} for ${dni}"
    
    if (response.json?.success) {
    	def lock = getLockDevice(dni)
        if (lock) {
        	def value = [1: "locked", 2: "unlocking", 3: "unlocked", 4: "locking"][response.json.state]
       		lock.sendEvent(name: "lock", value: value);
       }
    }
}

def getLockDevice(dni) {
	def lock = getChildDevices()?.find { it.deviceNetworkId == dni }
    if (!lock) {
       	log.warn "Could not find child device for DNI ${dni}"
    }
    
    return lock
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"

}

def installed() {
	log.debug "Installed"
    
    log.debug "Updating bridge network ID to ${device.currentValue("mac")}"
    device.setDeviceNetworkId("${device.currentValue("mac")}")
}

def uninstalled() {
	log.debug "Uninstalled"
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