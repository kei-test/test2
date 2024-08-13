package GInternational.server.api.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointTransactionDescriptionEnum {

    포인트전환("포인트 전환"),
    적립포인트("충전 적립 포인트");


    private String value;

}
