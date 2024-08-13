package GInternational.server.api.utilities;

import GInternational.server.api.dto.DifferenceStatisticAccountRequestDTO;

import java.util.List;

public class AuditContextHolder {
    private static final ThreadLocal<AuditContext> auditContext = new ThreadLocal<>();
    private static final ThreadLocal<List<DifferenceStatisticAccountRequestDTO>> context = new ThreadLocal<>();


    public static void setContext(AuditContext context) {
        auditContext.set(context);
    }

    public static AuditContext getContext() {
        AuditContext context = auditContext.get();
        if (context == null) {
            context = new AuditContext();
            auditContext.set(context);
        }
        return context;
    }

    public static void clear() {
        auditContext.remove();
    }

    public static void setAccounts(List<DifferenceStatisticAccountRequestDTO> accounts) {
        context.set(accounts);
    }

    public static List<DifferenceStatisticAccountRequestDTO> getAccounts() {
        return context.get();
    }
}
