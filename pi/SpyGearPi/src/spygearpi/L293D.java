package spygearpi;

// import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.wiringpi.SoftPwm;

public class L293D {

    // private GpioPinDigitalOutput leftPin01, leftPin02;
    // private GpioPinDigitalOutput rightPin01, rightPin02;
    private int leftPin01, leftPin02;
    private int rightPin01, rightPin02;
    private int minSpeed = 0, MaxSpeed = 100, halfSpeed = 50;
    
    //public L293D(GpioPinDigitalOutput... pins) {
    public L293D(int... pins) {
        if (pins != null && (pins.length == 2 || pins.length == 4)) {
            if (pins.length >= 2) {
                leftPin01 = pins[0];
                leftPin02 = pins[1];
                
                // left DC motor init.
                SoftPwm.softPwmCreate( leftPin01, minSpeed, MaxSpeed );
                SoftPwm.softPwmCreate( leftPin02, minSpeed, MaxSpeed );
        
            }

            if (pins.length == 4) {
                rightPin01 = pins[2];
                rightPin02 = pins[3];
                
                // right DC motor init.
                SoftPwm.softPwmCreate( rightPin01, minSpeed, MaxSpeed );
                SoftPwm.softPwmCreate( rightPin02, minSpeed, MaxSpeed );
            }            
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    
    public void leftForward() {
        SoftPwm.softPwmWrite(leftPin01, minSpeed);
        SoftPwm.softPwmWrite(leftPin02, MaxSpeed);
        // leftPin01.setState(false);
        // leftPin02.setState(true);
    }
    
    public void leftBackward() {
        SoftPwm.softPwmWrite(leftPin01, MaxSpeed);
        SoftPwm.softPwmWrite(leftPin02, minSpeed);
        // leftPin01.setState(true);
        // leftPin02.setState(false);
    }
    
    public void leftStop() {
        SoftPwm.softPwmWrite(leftPin01, minSpeed);
        SoftPwm.softPwmWrite(leftPin02, minSpeed);
        // leftPin01.setState(false);
        // leftPin02.setState(false);
    }
    
    public void leftForward_H() {
        SoftPwm.softPwmWrite(leftPin01, minSpeed);
        SoftPwm.softPwmWrite(leftPin02, halfSpeed);
        // leftPin01.setState(false);
        // leftPin02.setState(true);
    }
    
    public void leftBackward_H() {
        SoftPwm.softPwmWrite(leftPin01, halfSpeed);
        SoftPwm.softPwmWrite(leftPin02, minSpeed);
        // leftPin01.setState(true);
        // leftPin02.setState(false);
    }
    
    public void rightForward() {
        SoftPwm.softPwmWrite(rightPin01, minSpeed);
        SoftPwm.softPwmWrite(rightPin02, MaxSpeed);
        // rightPin01.setState(false);
        // rightPin02.setState(true);
    }
    
    public void rightBackward() {
        SoftPwm.softPwmWrite(rightPin01, MaxSpeed);
        SoftPwm.softPwmWrite(rightPin02, minSpeed);
        // rightPin01.setState(true);
        // rightPin02.setState(false);
    }
    
    public void rightStop() {
        SoftPwm.softPwmWrite(rightPin01, minSpeed);
        SoftPwm.softPwmWrite(rightPin02, minSpeed);
        // rightPin01.setState(false);
        // rightPin02.setState(false);
    }
    
    public void rightForward_H() {
        SoftPwm.softPwmWrite(rightPin01, minSpeed);
        SoftPwm.softPwmWrite(rightPin02, halfSpeed);
        // rightPin01.setState(false);
        // rightPin02.setState(true);
    }
    
    public void rightBackward_H() {
        SoftPwm.softPwmWrite(rightPin01, halfSpeed);
        SoftPwm.softPwmWrite(rightPin02, minSpeed);
        // rightPin01.setState(true);
        // rightPin02.setState(false);
    }
    
}
