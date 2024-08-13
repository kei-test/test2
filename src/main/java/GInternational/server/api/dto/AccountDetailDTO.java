package GInternational.server.api.dto;

import GInternational.server.api.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDetailDTO {

    private Long id;
    private String owner;
    private String bank;
    private String number;


    public AccountDetailDTO(Account account) {
        this.id = account.getId();
        this.owner = account.getOwner();
        this.bank = account.getBank();
        this.number = account.getNumber();
    }
}
