package GInternational.server.common.ipinfo.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpInfo {

    private String ip;
    private String country;
    private String deviceType;

    @Override
    public String toString() {
        return "IpInfo{" +
                "ip='" + ip + '\'' +
                ", country='" + country + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}
