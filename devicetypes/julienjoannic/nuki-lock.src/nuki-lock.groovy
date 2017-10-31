/**
 *  Nuki Lock
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
	definition (name: "Nuki Lock", namespace: "julienjoannic", author: "Julien JOANNIC") {
		capability "Lock"
        capability "Refresh"
        capability "Battery"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    	standardTile("lock", "device.lock", width: 3, height: 2, canChangeIcon: true) {
        	state "locked", label: '${currentValue}', action: "lock.unlock", icon: "st.locks.lock.locked", backgroundColor: "#00a0dc", nextState:"unlocking"
        	state "unlocked", label: '${currentValue}', action: "lock.lock", icon: "st.locks.lock.unlocked", backgroundColor: "#ffffff", nextState:"locking"
            state "locking", label: "locking", icon: "st.locks.lock.locked", backgroundColor: "#ffffff"
            state "unlocking", label: "unlocking", icon: "st.locks.lock.unlocked", backgroundColor: "#ffffff"
            state "unknown", label: "unknown", action: "lock.lock", icon: "st.locks.lock.unknown", backgroundColor: "#ffffff", nextState:"locking"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
          	state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
        }
        valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
          	state "default", label: "${currentValue}% battery"
        }
        
        main("lock")
        details(["lock", "refresh", "battery"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'lock' attribute

}

// handle commands
def lock() {
	log.debug "Executing 'lock'"
    parent.childLockAction(device.deviceNetworkId, 2)
}

def unlock() {
	log.debug "Executing 'unlock'"
    parent.childLockAction(device.deviceNetworkId, 1)
}

def setLockState(state) {
	sendEvent(name: "lock", value: state)
}

def refresh() {
	log.debug "Executing 'refresh'"
   	parent.childLockState(device.deviceNetworkId)
}