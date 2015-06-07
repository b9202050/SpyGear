package spygearpi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
//import com.pi4j.io.gpio.GpioPinDigitalOutput;
//import com.pi4j.io.gpio.RaspiPin;
//import com.pi4j.wiringpi.SoftPwm;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SpyGearPi {
    
    // MQTT topic
    private static final String TOPIC = "SpyGear";
    // MQTT broker ip
    private static String broker_host = "192.168.0.104";
    // MQTT broker port
    private static String broker_port = "1883";
    // MQTT client id
    private static final String clientId = "SpyGearPi";
    
    private static boolean exit = false;

    public static void main(String[] args) {
        
        // Assign MQTT broker ip
        if (args.length == 1) {
            broker_host = args[0];
        }
        
        // Assign MQTT client id
        if (args.length == 2) {
            broker_port = args[1];
        }     
        
        // Create Pi4J GpioController object
        final GpioController gpio = GpioFactory.getInstance();
        
        /*
        // Create left DC motor GPIO pin object
        final GpioPinDigitalOutput pin01 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21); //01
        final GpioPinDigitalOutput pin03 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22); //03
        
        // Create right DC motor GPIO pin object
        final GpioPinDigitalOutput pin00 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00); //00
        final GpioPinDigitalOutput pin02 = 
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02); //02
        */
        
        // left DC motor pin
        final int pin01 = 21, pin03 = 22;
        // right DC motor pin
        final int pin00 = 0, pin02 = 2;
                
        // Create L293D DC motor control object
        L293D l293d = new L293D(pin01, pin03, pin00, pin02);
        
        // Create MQTT service object
        final MqttService service = 
                new MqttService(broker_host, broker_port, clientId);
        
        // MQTT call back handler
        class MqttCallbackHandler implements MqttCallback {
        
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("TurtleCarPi Disconnect...");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) 
                    throws Exception {
                String message = new String(mqttMessage.getPayload());
                
                switch (message) {
                    // Forward
                    case "F":
                        l293d.leftForward();
                        l293d.rightForward();
                        break;
                    // Backward
                    case "B":
                        l293d.leftBackward();
                        l293d.rightBackward();
                        break;
                    // Turn left
                    case "L":
                        l293d.leftBackward();
                        l293d.rightForward();
                        break;
                    // Turn right
                    case "R":
                        l293d.leftForward();
                        l293d.rightBackward();
                        break;
                    // Stop
                    case "S":
                        l293d.leftStop();
                        l293d.rightStop();
                        break;
                    // Forward Half
                    case "f":
                        l293d.leftForward_H();
                        l293d.rightForward_H();
                        break;
                    // Backward Half
                    case "b":
                        l293d.leftBackward_H();
                        l293d.rightBackward_H();
                        break;
                    // Turn left Half
                    case "l":
                        l293d.leftBackward_H();
                        l293d.rightForward_H();
                        break;
                    // Turn right Half
                    case "r":
                        l293d.leftForward_H();
                        l293d.rightBackward_H();
                        break;
                    // Exit    
                    case "E":
                        exit = true;
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                
            }
        }
        
        // Register MQTT service call back handler
        service.setCallback(new MqttCallbackHandler());
        // Connect to MQTT broker
        service.connect();
        // Subscribe message
        service.subscribe(TOPIC);
        
        System.out.println("SpyGearPi Ready...");
        
        while (!exit) {
            delay(250);
        }
        
        System.out.println("SpyGearPi Bye...");
        
        service.disConnect();
        gpio.shutdown();
    }
    
    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }    
    
}
