metadata {
    definition (name: "Fibaro FGS-222 - Window Shade", namespace: "julienjoannic", author: "Julien Joannic") {
        capability "Switch"
        capability "Relay Switch"
        capability "Polling"
        capability "Configuration"
        capability "Refresh"
        capability "Zw Multichannel"
        capability "Window Shade"

        attribute "switch1", "string" // down
        attribute "switch2", "string" // up

        command "childOn"
        command "childOff"
        command "on1"
        command "off1"
        command "on2"
        command "off2"

        fingerprint deviceId: "0x1001", inClusters:"0x86, 0x72, 0x85, 0x60, 0x8E, 0x25, 0x20, 0x70, 0x27"
	}

    tiles(scale: 2) {
    	multiAttributeTile(name:"shade", type: "lighting", width: 6, height: 4) {
            tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState("unknown", label:'${name}', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e")
                attributeState("closed",  label:'${name}', action:"open", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd", nextState: "opening")
                attributeState("open",    label:'up', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33", nextState: "closing")
                attributeState("partially open", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
                attributeState("closing", label:'${name}', action:"stop", icon:"st.doors.garage.garage-closing", backgroundColor:"#bbbbdd")
                attributeState("opening", label:'${name}', action:"stop", icon:"st.doors.garage.garage-opening", backgroundColor:"#ffcc33")
            }
        }
        standardTile("switch1", "device.switch1",canChangeIcon: true, width: 2, height: 2) {
            state "on", label: "switch1", action: "off1", icon: "st.switches.switch.on", backgroundColor: "#79b821"
            state "off", label: "switch1", action: "on1", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        }
        standardTile("switch2", "device.switch2",canChangeIcon: true, width: 2, height: 2) {
            state "on", label: "switch2", action: "off2", icon: "st.switches.switch.on", backgroundColor: "#79b821"
            state "off", label: "switch2", action: "on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        }
        standardTile("refresh", "device.windowShade", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("configure", "device.windowShade", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"configure", icon:"st.secondary.configure"
        }

        main(["shade","switch1", "switch2"])
        details(["shade","switch1","switch2","refresh","configure"])
    }
   
    preferences {
        input name: "Info", type: "paragraph", title:"Device Handler by @RobinWinbourne and @cjcharles - Modified by @julienjoannic", description: "Parameter Settings:", displayDuringSetup: false

        input name: "param1", type: "number", range: "0..255", defaultValue: "255", required: false,
            title: "Parameter No. 1 - Activate / deactivate functions ALL ON / ALL OFF. \n" +
                   "255 - ALL ON active, ALL OFF active,\n" +
                   "0 - ALL ON is not active ALL OFF is not active,\n" +
                   "1 - ALL ON is not active ALL OFF active,\n" +
                   "2 - ALL ON active ALL OFF is not active.\n" +
                   "Default value: 255."

        input name: "param3", type: "number", range: "0..1", defaultValue: "0", required: false,
            title: "Parameter No. 3 - Auto off relay after specified time, with the possibility of manual override - immediate Off after button push.  " +
                   "Available settings:\n" +
                   "0 - manual override disabled. After single button push the relay turns on and automatically turns off after specified time.,\n" +
                   "1 - manual override enabled. After single button push the relay turns on and automatically turns off after specified time. Another button push turns the relay off immediately..\n" +
                   "Default value: 0."

        input name: "param4", type: "number", range: "0..65535", defaultValue: "0", required: false,
            title: "Parameter No. 4 - Auto off for relay 1.  " +
                   "Available settings:\n" +
                   "[1 - 65535] (0,1 s – 6553,5 s) Time period for auto off, in miliseconds,\n" +
                   "0 - Auto off disabled.\n" +
                   "Default value: 0."

        input name: "param5", type: "number", range: "0..65535", defaultValue: "0", required: false,
            title: "Parameter No. 5 - Auto off for relay 2.  " +
                   "Available settings:\n" +
                   "[1 - 65535] (0,1 s – 6553,5 s) Time period for auto off, in miliseconds,\n" +
                   "0 - Auto off disabled.\n" +
                   "Default value: 0."

       input name: "param6", type: "number", range: "0..2", defaultValue: "0", required: false,
            title: "Parameter No. 6 - Sending commands to control devices assigned to 1-st association group (key no. 1).  " +
                   "NOTE: Parameter 15 value must be set to 1 to work properly. This activates the double-click functionality - dimmer/roller shutter control.\n" +
                   "Available settings:\n" +
                   "0 - commands are sent when device is turned on and off,\n" +
                   "1 - commands are sent when device is turned off. Enabling device does not send control commands. Double-clicking key sends 'turn on' command, dimmers memorize the last saved state (e.g. 50% brightness),\n" +
                   "2 - commands are sent when device is turned off. Enabling device does not send control commands. Double-clicking key sends 'turn on' command and dimmers are set to 100% brightness.\n" +
                   "Default value: 0."

		input name: "param7", type: "number", range: "0..2", defaultValue: "0", required: false,
            title: "Parameter No. 7 - Sending commands to control devices assigned to 2-st association group (key no. 2).  " +
                   "NOTE: Parameter 15 value must be set to 1 to work properly. This activates the double-click functionality - dimmer/roller shutter control.\n" +
                   "Available settings:\n" +
                   "0 - commands are sent when device is turned on and off,\n" +
                   "1 - commands are sent when device is turned off. Enabling device does not send control commands. Double-clicking key sends 'turn on' command, dimmers memorize the last saved state (e.g. 50% brightness),\n" +
                   "2 - commands are sent when device is turned off. Enabling device does not send control commands. Double-clicking key sends 'turn on' command and dimmers are set to 100% brightness.\n" +
                   "Default value: 0."

        input name: "param13", type: "number", range: "0..1", defaultValue: "0", required: false,
            title: "Parameter No. 13 - Assigns bistable key status to the device.  " +
                   "Available settings:\n" +
                   "0 - [On / Off] device changes status on key status change,\n" +
                   "1 - Device status depends on key status: ON when the key is ON.\n" +
                   "Default value: 0."

        input name: "param14", type: "number", range: "0..1", defaultValue: "1", required: false,
            title: "Parameter No. 14 - Switch type connector, you may choose between momentary and toggle switches.  " +
                   "Available settings:\n" +
                   "0 - momentary switch,\n" +
                   "1 - toggle switch.\n" +
                   "Default value: 1."

        input name: "param15", type: "number", range: "0..1", defaultValue: "0", required: false,
            title: "Parameter No. 15 - Operation of the Dimmer and Roller Shutter Controller - enabling this option allows the user to dim lighting/shut roller by associating Dimmer/Roller Shutter Controller and holding or double press of double switch (only mono-stable switch).  " +
                   "Available settings:\n" +
                   "0 - Dimmer/Roller Shutter Controller control is not active,\n" +
                   "1 - Dimmer/Roller Shutter Controller control is active.\n" +
                   "Default value: 0."

        input name: "param16", type: "number", range: "0..1", defaultValue: "1", required: false,
            title: "Parameter No. 16 - Saving the state of the device after a power failure. Fibaro Switch returns to the last position saved before a power failure.  " +
                   "Available settings:\n" +
                   "0 - Fibaro Switch does not save the state after a power failure, it returns to 'off' position,\n" +
                   "1 - Fibaro Switch saves its state before power failure.\n" +
                   "Default value: 1."

        input name: "param30", type: "number", range: "0..3", defaultValue: "3", required: false,
            title: "Parameter No. 30 - General Alarm, set for relay no. 1.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 3."

        input name: "param31", type: "number", range: "0..3", defaultValue: "2", required: false,
            title: "Parameter No. 31 - Alarm of flooding with water, set for relay no. 1.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 2."

        input name: "param32", type: "number", range: "0..3", defaultValue: "3", required: false,
            title: "Parameter No. 32 - Smoke, CO, CO2 Alarm. Set for relay no. 1.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 3."

        input name: "param33", type: "number", range: "0..3", defaultValue: "1", required: false,
            title: "Parameter No. 33 - Temperature Alarm, set for relay no. 1.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 1."

        input name: "param40", type: "number", range: "0..3", defaultValue: "3", required: false,
            title: "Parameter No. 40 - General Alarm, set for relay no. 2.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 3."

        input name: "param41", type: "number", range: "0..3", defaultValue: "2", required: false,
            title: "Parameter No. 41 - Alarm of flooding with water, set for relay no. 2.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 2."

        input name: "param42", type: "number", range: "0..3", defaultValue: "3", required: false,
            title: "Parameter No. 42 - Smoke, CO, CO2 Alarm. Set for relay no. 2.  " +
                   "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 3."

        input name: "param43", type: "number", range: "0..3", defaultValue: "1", required: false,
            title: "Parameter No. 43 - Temperature Alarm, set for relay no. 2.  " +
                  "Available settings:\n" +
                   "0 - DEACTIVATION - the device does not respond to alarm data frames\n" +
                   "1 - ALARM RELAY ON - the device turns on after detecting an alarm,\n" +
                   "2- ALARM RELAY OFF - the device turns off after detecting an alarm,\n" +
                   "3 - ALARM FLASHING - the device periodically changes its status to the opposite, when it detects an alarm within 10 min.\n" +
                   "Default value: 1."

        input name: "param39", type: "number", range: "0..65535", defaultValue: "600", required: false,
            title: "Parameter No. 39 - Active flashing alarm time. " +
                   "This parameter allows to set time parameter used in timed modes.\n" +
                   "Available settings:\n" +
                   "[1-65535][ms].\n" +
                   "Default value: 600."

/*    input name: "paramAssociationGroup1", type: "bool", defaultValue: true, required: true,
             title: "The Fibaro Sigle Switch provides the association of three groups.\n\n" +
                    "2nd group is assigned to key no. 1.\n" +
                    "Default value: true"

        input name: "paramAssociationGroup2", type: "bool", defaultValue: true, required: true,
             title: "2nd group is assigned to key no. 2.\n" +
                    "Default value: true"

        input name: "paramAssociationGroup3", type: "bool", defaultValue: false, required: true,
             title: "3rd group reports state of devices. Only one device can be associated to this group.\n" +
                    "Default value: false"*/
    }
}

def parse(String description) {
    def result = []
    def cmd = zwave.parse(description)
    if (cmd) {
        result += zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {

	sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {

    sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd) {
    log.debug "multichannelv3.MultiChannelCapabilityReport $cmd"
    
    if (cmd.endPoint == 2) {
        def currstate = device.currentState("switch2").getValue()
        if (currstate == "on") {
        	sendEvent(name: "switch2", value: "off", isStateChange: true, display: false)
        }
        else if (currstate == "off") {
        	sendEvent(name: "switch2", value: "on", isStateChange: true, display: false)
            sendEvent(name: "windowShade", value: "opening")
        }
    }
    else if (cmd.endPoint == 1 ) {
        def currstate = device.currentState("switch1").getValue()
        if (currstate == "on") {
        	sendEvent(name: "switch1", value: "off", isStateChange: true, display: false)
            
        }
        else if (currstate == "off") {
        	sendEvent(name: "switch1", value: "on", isStateChange: true, display: false)
            sendEvent(name: "windowShade", value: "closing")
        }
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
   def map = [ name: "switch$cmd.sourceEndPoint" ]
   def curswitch, curstate
   
   switch(cmd.commandClass) {
      case 32:
         if (cmd.parameter == [0]) {
            map.value = "off"
         }
         if (cmd.parameter == [255]) {
            map.value = "on"
         }
         createEvent(map)
         break
      case 37:
         if (cmd.parameter == [0]) {
            map.value = "off"
         }
         if (cmd.parameter == [255]) {
            map.value = "on"
         }
         curstate = map.value
         curswitch = cmd.sourceEndPoint
         break
    }
    
    //Now if there is a child device then send it a state update
    try {
        def childDevice = getChildDevices()?.find { it.deviceNetworkId == "$device.deviceNetworkId-sw${curswitch}"}
           if (childDevice)
              childDevice.sendEvent(name: "switch", value: curstate)
    } catch (e) {
        log.error "Couldn't find child device, probably doesn't exist and hence no problem: ${e}"
    }
    
    def events = [createEvent(map)]
    if (map.value == "on") {
            events += [createEvent([name: "switch", value: "on"])]
    } else {
         def allOff = true
         (1..2).each { n ->
             if (n != cmd.sourceEndPoint) {
                 if (device.currentState("switch${n}").value != "off") allOff = false
             }
         }
         if (allOff) {
             events += [createEvent([name: "switch", value: "off"])]
         }
    }
    events
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    // This will capture any commands not handled by other instances of zwaveEvent
    // and is recommended for development so you can see every command the device sends
    return createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

def zwaveEvent(physicalgraph.zwave.commands.switchallv1.SwitchAllReport cmd) {
   log.debug "SwitchAllReport $cmd"
}

def refresh() {
	def cmds = []
    cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet().format()
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
	delayBetween(cmds, 1000)
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	log.debug "msr: $msr"
    updateDataValue("MSR", msr)
}

def poll() {
	def cmds = []
	cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
	delayBetween(cmds, 1000)
}

def configure() {
	log.debug "Executing 'configure'"
    delayBetween([
          zwave.configurationV1.configurationSet(parameterNumber:1, configurationValue:[param1.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:3, configurationValue:[param3.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:4, configurationValue:[param4.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:5, configurationValue:[param5.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:6, configurationValue:[param6.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:7, configurationValue:[param7.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:13, configurationValue:[param13.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:14, configurationValue:[param14.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:15, configurationValue:[param15.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:16, configurationValue:[param16.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:30, configurationValue:[param30.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:31, configurationValue:[param31.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:32, configurationValue:[param32.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:33, configurationValue:[param33.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:39, configurationValue:[param39.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:40, configurationValue:[param40.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:41, configurationValue:[param41.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:42, configurationValue:[param42.value]).format(),
          zwave.configurationV1.configurationSet(parameterNumber:43, configurationValue:[param43.value]).format(),
          zwave.associationV2.associationSet(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format(),
          zwave.associationV2.associationSet(groupingIdentifier:2, nodeId:[zwaveHubNodeId]).format(),
          zwave.associationV2.associationSet(groupingIdentifier:3, nodeId:[zwaveHubNodeId]).format(),
    ])
}

/**
* Triggered when Done button is pushed on Preference Pane
*/
def updated()
{
	log.debug "Preferences have been changed. Attempting configure() and update"
    def cmds = configure()
	
    response(cmds)
}

def open() { 
	def cmds = []
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[0]).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:1, parameter:[255]).format()
    cmds << "delay 1000"
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    
	return cmds
}

def close() {
	def cmds = []
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[255]).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:1, parameter:[0]).format()
    cmds << "delay 1000"
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    
	return cmds
}

def stop() {
	def cmds = []
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[255]).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:1, parameter:[255]).format()
    cmds << "delay 500"
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[0]).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:1, parameter:[0]).format()
    cmds << "delay 1000"
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    cmds << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    
	return cmds
}

def on1() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[255]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    ], 1000)
}

def off1() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[0]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    ], 1000)
}

def on2() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:1, parameter:[255]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:2).format()
    ], 1000)
}

def off2() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:1, parameter:[0]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:2, destinationEndPoint:2, commandClass:37, command:2).format()
    ], 1000)
}