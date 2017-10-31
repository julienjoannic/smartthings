/**
 *  Car (Dash)
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
	definition (name: "Car (Dash)", namespace: "julienjoannic", author: "Julien JOANNIC") {
		capability "Switch"
	}

	tiles {
    	standardTile("switch", "device.switch", width: 3, height: 2, canChangeIcon: true) {
        	state "on", label: '${currentValue}', icon: "st.bmw.doors-none-open", backgroundColor: "#00a0dc"
        	state "off", label: '${currentValue}', icon: "st.bmw.doors-none-open", backgroundColor: "#ffffff"
        	state "unknown", label: '${currentValue}', icon: "st.bmw.doors-none-open"
        }
	}
}