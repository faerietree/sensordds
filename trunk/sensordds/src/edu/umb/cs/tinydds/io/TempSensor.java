package edu.umb.cs.tinydds.io;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITemperatureInput;
import java.io.IOException;

/**
 *
 * @author matt
 */
public class TempSensor {
    private ITemperatureInput tempSensor = EDemoBoard.getInstance().getADCTemperature();

    public int getValue() throws IOException {
        return tempSensor.getValue();
    }
}
