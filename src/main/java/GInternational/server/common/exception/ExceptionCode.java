package GInternational.server.common.exception;

import lombok.Getter;


public enum ExceptionCode {
    ACCOUNT_ALREADY_EXISTS(404, "등록 신청한 계좌가 있습니다."),
    AUTOMATIC_ACCOUNT_APPROVAL_INFO_NOT_FOUND(404, "자동승인 계좌정보 없음"),
    ALREADY_APPLIED_TODAY(400, "오늘 이미 신청했습니다."),
    APPLE_ALREADY_PLAYED(400, "사과줍기 게임을 이미 했습니다."),
    APPLE_FAILED(500, "사과줍기 게임 실행 실패."),
    APPLE_PROBABILITY_EXCEEDED(400, "사과줍기 확률 100% 초과."),
    APPLE_RESULT_NOT_FOUND(404, "사과줍기 결과를 찾을 수 없습니다."),
    APPLE_SETTING_NOT_FOUND(404, "사과줍기 설정을 찾을 수 없습니다."),
    APPLIED_TRANSACTION_NOT_FOUNT(404, "신청건을 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(404,"신청 내역이 없습니다."),
    ARTICLE_NOT_FOUND(404, "Article not found"),
    ATTENDANCE_RESULT_NOT_FOUND(404, "출석체크 룰렛 결과를 찾을 수 없습니다."),
    ATTENDANCE_ROULETTE_ALREADY_SPUN(400, "출석체크 룰렛을 이미 돌렸습니다."),
    ATTENDANCE_ROULETTE_PROBABILITY_EXCEEDED(400, "출석체크 룰렛 확률 100% 초과."),
    ATTENDANCE_ROULETTE_SETTING_NOT_FOUND(404, "출석체크 룰렛 설정을 찾을 수 없습니다."),
    ATTENDANCE_ROULETTE_SPIN_FAILED(500, "출석체크 룰렛 스핀이 실패 했습니다."),
    BLOCKED_IP(404, "접근이 차단된 ip입니다."),
    BALANCE_TOO_HIGH(400, "캐시 1만원 이상 보유중이면 신청 불가합니다."),
    BET_AMOUNT_TOO_LOW(400, "어제 슬롯 게임에서 배팅한 총 금액이 5만원 미만이면 신청 불가합니다."),
    CANNOT_RECOMMEND(400, "해당 추천인은 추천이 불가능한 유저입니다."),
    CATEGORY_NOT_FOUND(404, "Category not found"),
    COMMENT_NOT_FOUND(404, "Comment not found"),
    DEPOSIT_TRANSACTION_NOT_FOUND(404,"신청 거래내역이 없습니다."),
    DATA_INTEGRITY_VIOLATION(400, "데이터 유효성 오류가 발생했습니다."),
    DATA_NOT_FOUND(400, "데이터가 없습니다."),
    DEDICATED_ACCOUNT_NOT_FOUND(400, "전용계좌를 찾을 수 없습니다."),
    DUPLICATE_PHONE(400, "이미 존재하는 핸드폰번호입니다."),
    DUPLICATE_ENTRY(400, "이미 차단된 아이피입니다."),
    DEBIT_NOT_FOUND(404, "거래 내역이 없습니다."),
    EVENT_NOT_FOUND(404, "이벤트를 찾을 수 없습니다."),
    GAME_NOT_FOUNT(404, "게임을 찾을 수 없습니다"),
    INVALID_USERNAME(404, "아이디에 띄어쓰기를 포함 할 수 없습니다."),
    INSUFFICIENT_CASINO_MONEY(404, "카지노머니가 부족합니다."),
    INSUFFICIENT_SPORTS_MONEY(404, "스포츠머니가 부족합니다"),
    INSUFFICIENT_POINTS(404, "포인트가 부족합니다"),
    INSUFFICIENT_FUNDS_OR_INVALID_AMOUNT(404, "충전 금액이 0보다 커야하고 지갑 잔액이 충분해야 합니다."),
    INQUIRY_NOT_FOUND(404, "문의를 찾을 수 없습니다."),
    INTERNAL_ERROR(500, "내부 오류입니다."),
    INVALID_BET_AMOUNT(404, "잘못된 베팅 금액 입니다."),
    INVALID_COUPON_TYPE(400, "잘못된 쿠폰 종류입니다."),
    INVALID_EVENT_DESCRIPTION(400, "이벤트 설명을 입력해야 합니다."),
    INVALID_EVENT_TITLE(400, "이벤트 제목을 입력해야 합니다."),
    INVALID_EXP_RANGE(400,"경험치 범위에 부합하지 않아 레벨업을 신청할 수 없습니다."),
    INVALID_LEVEL(404, "레벨은 1부터 10 사이의 값이어야 합니다."),
    INVALID_OPERATION(404, "invalid operation"),
    INVALID_REQUEST(404, "잘못된 요청입니다"),
    INVALID_STATUS(404, "유효하지 않은 상태입니다."),
    INVALID_TRANSACTION_STATE(400, "처리할 수 없는 상태의 신청건입니다."),
    INVALID_USER_LEVEL(400, "접근 권한이 없는 레벨입니다."),
    LIMIT_EXCEEDED(400, "일일 게시글이나 댓글 등록가능 갯수를 초과했습니다."),
    MATCH_NOT_FOUND(404, "MATCH_NOT_FOUND"),
    MESSAGE_NOT_FOUND(404, "Message not found"),
    MONEY_COUPON_EXPIRED(400, "쿠폰의 유효기간이 만료되었습니다."),
    NO_NEXT_ARTICLE(400, "다음 게시글이 없습니다."),
    NO_PREVIOUS_ARTICLE(400, "이전 게시글이 없습니다."),
    NOTICE_NOT_FOUND(404, "공지사항을 찾을 수 없습니다."),
    ONLY_WAITING_TRANSACTIONS_CAN_BE_APPROVED(404, "대기중인 내역만 승인가능합니다."),
    PASSWORD_NOT_MATCH(400, "기존 비밀번호가 일치하지 않습니다."),
    PERMISSION_DENIED(400, "권한이 없습니다."),
    RECOMMENDATION_CODE_ALREADY_ISSUED(400, "해당 유저는 이미 추천인코드가 발급되었습니다."),
    REFERRER_NOT_FOUNT(400, "추천인을 찾을 수 없습니다"),
    ROULETTE_ALREADY_SPUN(400, "룰렛을 이미 돌렸습니다."),
    ROULETTE_PROBABILITY_EXCEEDED(400, "룰렛 확률 100% 초과."),
    ROULETTE_RESULT_NOT_FOUND(404, "룰렛 결과를 찾을 수 없습니다."),
    ROULETTE_SETTING_NOT_FOUND(404, "룰렛 설정을 찾을 수 없습니다."),
    ROULETTE_SPIN_FAILED(500, "룰렛 스핀이 실패했습니다."),
    SETTING_NOT_FOUND(404, "셋팅값을 찾을 수 없습니다"),
    START_DATE_AFTER_END_DATE(400, "시작 날짜가 종료 날짜보다 늦을 수 없습니다."),
    START_DATE_IN_PAST(400, "시작 날짜가 과거일 수 없습니다."),
    TRANSACTION_ALREADY_PROCESSED(400, "이미 처리된 신청건입니다."),
    UNAUTHORIZED_ACCESS(401, "권한이 없습니다."),
    USERNAME_DUPLICATE(404, "중복된 닉네임 입니다."),
    USERNAME_NOT_PROVIDED(400, "닉네임을 입력하세요"),
    USER_EXIST(404, "User exists"),
    USER_LEVEL_NOT_ALLOWED(400, "8,9 레벨은 신청 불가합니다."),
    USER_NOT_FOUND(404, "User not found"),
    WALLET_NOT_FOUND(404, "지갑을 찾을 수 없습니다."),
    WALLET_INFO_NOT_FOUND(404, "금액 정보 없음");


    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
