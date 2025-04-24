package model;

import utils.Validator;

/**
 * Represents a room type in a Build-To-Order (BTO) project, storing the flat type, number of available
 * units, and price. Provides methods to access and modify room details and convert to CSV format.
 *
 * @author SC2002Team
 */
public class Room {
    private String room_type;
    private int units;
    private int price;

    /**
     * Constructs a Room with the specified flat type, number of units, and price.
     *
     * @param type The flat type (e.g., "2-room", "3-room").
     * @param units The number of available units.
     * @param price The price of the flat type.
     * @throws IllegalArgumentException If the flat type is invalid, units are negative, or price is non-positive.
     */
    public Room(String type, int units, int price) {
        if (!Validator.isValidFlatType(type)) {
            throw new IllegalArgumentException("Invalid flat type: Must be a valid flat type (e.g., '2-room', '3-room').");
        }
        if (units < 0) {
            throw new IllegalArgumentException("Invalid units: Must be non-negative.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price: Must be positive.");
        }

        this.room_type = type.trim();
        this.units = units;
        this.price = price;
    }

    /**
     * Gets the flat type.
     *
     * @return The flat type.
     */
    public String getRoomType() {
        return room_type;
    }

    /**
     * Sets the flat type.
     *
     * @param type The new flat type.
     * @throws IllegalArgumentException If the flat type is invalid.
     */
    public void setRoomType(String type) {
        if (!Validator.isValidFlatType(type)) {
            throw new IllegalArgumentException("Invalid flat type: Must be a valid flat type (e.g., '2-room', '3-room').");
        }
        this.room_type = type.trim();
    }

    /**
     * Gets the number of available units.
     *
     * @return The number of units.
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the number of available units.
     *
     * @param unitsleft The new number of units.
     * @throws IllegalArgumentException If the number of units is negative.
     */
    public void setUnits(int unitsleft) {
        if (unitsleft < 0) {
            throw new IllegalArgumentException("Invalid units: Must be non-negative.");
        }
        this.units = unitsleft;
    }

    /**
     * Gets the price of the flat type.
     *
     * @return The price.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of the flat type.
     *
     * @param price The new price.
     * @throws IllegalArgumentException If the price is non-positive.
     */
    public void setPrice(int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price: Must be positive.");
        }
        this.price = price;
    }

    /**
     * Converts the room data to a CSV-compatible string.
     *
     * @return A comma-separated string of room type, units, and price.
     * @throws IllegalStateException If the room type is null.
     */
    public String toCSV() {
        if (room_type == null) {
            throw new IllegalStateException("Room type cannot be null for CSV output.");
        }
        return String.join(",", room_type, Integer.toString(units), Integer.toString(price));
    }

}
