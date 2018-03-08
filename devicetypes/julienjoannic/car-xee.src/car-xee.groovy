/**
 *  Car (Xee)
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
	definition (name: "Car (Xee)", namespace: "julienjoannic", author: "Julien JOANNIC") {
		capability "Switch"
        capability "PresenceSensor"
        capability "Refresh"
        
        attribute "status", "enum", ["home", "leaving", "out", "arriving"]
        attribute "distance", "number"
        attribute "odometer", "number"
        attribute "headLights", "string"
        attribute "lock", "string"
        attribute "fuel", "number"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		standardTile("switch", "device.switch", width: 3, height: 2, canChangeIcon: true) {
        	state "on", label: '${currentValue}', icon: "st.bmw.doors-none-open", backgroundColor: "#00a0dc"
        	state "off", label: '${currentValue}', icon: "st.bmw.doors-none-open", backgroundColor: "#ffffff"
        	state "unknown", label: '${currentValue}', icon: "st.bmw.doors-none-open"
        }
        standardTile("status", "device.status", width: 1, height: 1) {
        	state "out", label: 'Out', icon: "st.Transportation.transportation13", backgroundColor: "#ffffff" 
        	state "home", label: 'Home', icon: "st.Transportation.transportation12", backgroundColor: "#00a0dc" 
        	state "leaving", label: 'Leaving', icon: "st.bmw.windows-down", backgroundColor: "#90d2a7" 
        	state "arriving", label: 'Arriving', icon: "st.bmw.windows-passenger-down", backgroundColor: "#f1d801" 
        }
        standardTile("presence", "device.presence", width: 1, height: 1) {
        	state "present", label: '${currentValue}', backgroundColor: "#00a0dc" 
        	state "not present", label: '${currentValue}', backgroundColor: "#ffffff" 
        }
        standardTile("distance", "device.distance", width: 1, height: 1) {
        	state "default", label: '${currentValue}m', unit: "m", icon: "st.motion.motion-detector.recent"
        }
        standardTile("odometer", "device.odometer", width: 1, height: 1) {
        	state "default", label: 'Odometer\n${currentValue}', unit: "km", icon: ""
        }
        standardTile("fuel", "device.fuel", width: 1, height: 1) {
        	state "default", label: 'Fuel level: ${currentValue}', unit: "L", icon: ""
        }
        standardTile("headLights", "device.headLights", width: 1, height: 1) {
        	state "on", label: '', icon: 'st.illuminance.illuminance.bright', backgroundColor: "#f1d801"
        	state "off", label: '', icon: 'st.illuminance.illuminance.dark', backgroundColor: "#ffffff"
        }
        standardTile("lock", "device.lock", width: 1, height: 1) {
        	state "locked", label: '${currentValue}', icon: "st.bmw.doors_locked", backgroundColor: "#90d2a7"
            state "unlocked", label: '${currentValue}', icon: "st.bmw.doors_unlocked", backgroundColor: "#d04e00"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
          	state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
        }
	}
}

def getOnOffMap() {
	return [0: "off", 1: "on"]
}

def update(Map data) {
	def switchValue = "off";
	data?.signals?.each { signal ->
        switch(signal.name) {
            //case "IgnitionSts":
            case "ComputedAccActivity":
            	log.debug "${signal.name} is ${signal.value}"
            	if (signal.value == 1) {
                	switchValue = "on";
                }
            	break
            
            case "EngineSpeed":
            	log.debug "Engine speed is ${signal.value}"
            	if (signal.value > 0) {
                	switchValue = "on";
                }
                break

            case "HeadLightSts":
            sendEvent(name: "headLights", value: onOffMap[signal.value])
            break

            case "LockSts":
            sendEvent(name: "lock", value: [0: "unlocked", 1: "locked"][signal.value])
            break

            case "FuelLevel":
            sendEvent(name: "fuel", value: signal.value)
            break

            case "Odometer":
            sendEvent(name: "odometer", value: signal.value)
            break
        }
    }
    
    log.debug "Switch value is ${switchValue}"
    sendEvent(name: 'switch', value: switchValue)
        
    def distance = getDistance(data?.location?.latitude, data?.location?.longitude)
    log.debug "Distance from home is ${distance}m"
    sendEvent(name: "distance", value: distance);
    
    if (device.currentValue("switch") == "off" && (distance < 60)) {
        sendEvent(name: "presence", value: "present")
        sendEvent(name: "status", value: "home")
    }
    else {
        sendEvent(name: "presence", value: "not present")
         
    	switch(device.currentValue("status")) {
        	case "home":
         		sendEvent(name: "status", value: "leaving")
                break
              
            case "arriving":
            case "leaving":
            	if ((device.currentValue("switch") == "off") || (distance > 200)) {
                	sendEvent(name: "status", value: "out")
                }
                break
                
            case "out":
            	if ((device.currentValue("switch") == "on")  && (distance < 150)) {
                	sendEvent(name: "status", value: "arriving")
                }
                break
                
            default:
            	sendEvent(name: "status", value: "out")
                break
        }
    }
}

def refresh() {
	update(parent.poll(device.deviceNetworkId))
}

def getDistance(latitude, longitude)  {
    def R = 6371; // Radius of the earth in km
    def dLat = toRadians(location.latitude-latitude);  // deg2rad below
    def dLon = toRadians(location.longitude-longitude);
    
    def a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        	Math.cos(toRadians(location.latitude)) * Math.cos(toRadians(latitude)) * 
        	Math.sin(dLon/2) * Math.sin(dLon/2);

    def c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    def d = R * c; // Distance in km
    return Math.round(d * 1000);
}

def toRadians(deg)  {
    return deg * (Math.PI/180);
}