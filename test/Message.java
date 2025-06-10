package test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    /**
     * This is our main constructor
     *
     * @param data - Message data in bytes
     */
    public Message(byte[] data) {
        this.data = data;
        this.asText = new String(data, StandardCharsets.UTF_8);
        this.asDouble = convertToDouble(this.asText);
        this.date = new Date();
    }

    /**
     * Constructor get String parameter
     * and calls our main constructor
     *
     * @param asText - Message data as string
     */
    public Message(String asText) {
        this(asText.getBytes());
    }

    /**
     * Constructor get Double parameter
     * and calls our main constructor
     *
     * @param asDouble - Message data as double
     */
    public Message(double asDouble) {
        this(Double.toString(asDouble));
    }

    // Extra functions

    /**
     * The function gets data as String
     * and try to convert it into Double
     * if can't return NaN
     *
     * @param data - Message data as string
     * @return data as double or NaN
     */
    private double convertToDouble(String data) {
        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

}
