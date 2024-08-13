package GInternational.server.api.repository;

import GInternational.server.api.entity.Account;
import GInternational.server.api.vo.AppStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountRepositoryCustom{

    Page<Account> searchByAppStatus(AppStatus status, Pageable pageable);
}
