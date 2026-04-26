package energy.javafx_gui.dto;

public class HistoricalEnergyDto {
    private String hour;
    private double community_produced;
    private double community_used;
    private double grid_used;

    public HistoricalEnergyDto() {
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public double getCommunity_produced() {
        return community_produced;
    }

    public void setCommunity_produced(double community_produced) {
        this.community_produced = community_produced;
    }

    public double getCommunity_used() {
        return community_used;
    }

    public void setCommunity_used(double community_used) {
        this.community_used = community_used;
    }

    public double getGrid_used() {
        return grid_used;
    }

    public void setGrid_used(double grid_used) {
        this.grid_used = grid_used;
    }
}
