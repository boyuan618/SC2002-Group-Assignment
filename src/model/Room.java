package model;

public class Room {
    private String room_type;
    private int units;
    private int price;

    public Room(String type, int units, int price) {
        this.room_type = type;
        this.units = units;
        this.price = price;
    }

    public String getRoomType() {
        return room_type;
    }

    public void setRoomType(String type) {
        this.room_type = type;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int unitsleft) {
        this.units = unitsleft;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int Price) {
        this.price = Price;
    }

    public String toCSV() {
        return String.join(",", room_type, Integer.toString(units), Integer.toString(price));
    }

}
