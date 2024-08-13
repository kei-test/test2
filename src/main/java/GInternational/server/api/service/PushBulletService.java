package GInternational.server.api.service;

import GInternational.server.api.entity.WebSocketMessage;
import GInternational.server.api.repository.WebSocketMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import okio.ByteString;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class PushBulletService {
    private static final List<String> INCOME_KEYWORDS = Arrays.asList("입금", "지급", "전자금융입금", "CMS입금", "무통장입금", "창구입금");
    private final String accessToken = "o.QwhLHbkGhzGYk8nmRlAEdBRPLIg0br9S";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketMessageRepository webSocketMessageRepository;
    private final RechargeService rechargeService;

    private WebSocket webSocket;
    private boolean isWebSocketConnected = false;

    public void startWebSocket() {
        Request request = new Request.Builder()
                .url("wss://stream.pushbullet.com/websocket/" + accessToken)
                .build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isWebSocketConnected = true;
                System.out.println("WebSocket Connected!");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Received: " + text);
                handleIncomingMessage(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                String hexMessage = bytes.hex();
                System.out.println("Received bytes: " + hexMessage);
                handleIncomingMessage(hexMessage);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                isWebSocketConnected = false;
                webSocket.close(1000, null);
                System.out.println("Closing : " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isWebSocketConnected = false;
                System.out.println("Error : " + t.getMessage());
            }
        };

        this.webSocket = client.newWebSocket(request, listener);
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkWebSocketConnection() {
        if (!isWebSocketConnected) {
            System.out.println("WebSocket is not connected. Reconnecting...");
            startWebSocket();
        } else {
            System.out.println("WebSocket is connected.");
        }
    }

    private void handleIncomingMessage(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String type = rootNode.path("type").asText();
            System.out.println("Message type: " + type);
            if ("push".equals(type)) {
                JsonNode pushNode = rootNode.path("push");
                String pushType = pushNode.path("type").asText();
                System.out.println("Push type: " + pushType);
                if ("sms_changed".equals(pushType)) {
                    JsonNode notificationNode = pushNode.path("notifications").get(0);
                    long timestamp = notificationNode.path("timestamp").asLong();
                    String phoneNumber = notificationNode.path("title").asText();
                    String body = notificationNode.path("body").asText();
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("Phone number: " + phoneNumber);
                    System.out.println("Body: " + body);

                    // 입금 관련 메시지 검증
                    if (!isIncomeRelatedMessage(body)) {
                        return;
                    }

                    String amount = extractAmountFromBody(body);
                    String depositor = extractDepositorFromBody(body);

                    System.out.println("Amount: " + amount);
                    System.out.println("Depositor: " + depositor);

                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());

                    WebSocketMessage webSocketMessage = new WebSocketMessage();
                    webSocketMessage.setTimestamp(dateTime);
                    webSocketMessage.setPhone(phoneNumber);
                    webSocketMessage.setDepositor(depositor);
                    webSocketMessage.setAmount(amount);
                    webSocketMessage.setMessage(body);
                    saveMessage(webSocketMessage);

                    boolean isApproved = rechargeService.autoApprovalBasedOnMessage(amount, depositor, body, dateTime);

                    if (isApproved) {
                        System.out.println("자동충전 승인 처리가 성공적으로 완료되었습니다.");
                    } else {
                        System.out.println("일치하는 충전 신청 내역을 찾지 못했거나 여러 건이 있습니다.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(WebSocketMessage message) {
        try {
            webSocketMessageRepository.save(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractAmountFromBody(String body) {
        String amount = "";

        // 패턴 목록
        Pattern[] patterns = {
                // 쉼표가 있는 형식
                Pattern.compile("입금\\s?(\\d{1,3}(?:,\\d{3})*)(원)?"), // 패턴: 입금 (쉼표 있음)
                Pattern.compile("지급\\s?(\\d{1,3}(?:,\\d{3})*)(원)?"), // 패턴: 지급 (쉼표 있음)
                Pattern.compile("전자금융입금\\s?(\\d{1,3}(?:,\\d{3})*)(원)?"), // 패턴: 전자금융입금 (쉼표 있음)
                Pattern.compile("CMS입금\\s?(\\d{1,3}(?:,\\d{3})*)(원)?"), // 패턴: CMS입금 (쉼표 있음)
                Pattern.compile("무통장입금\\s?(\\d{1,3}(?:,\\d{3})*)(원)?"), // 패턴: 무통장입금 (쉼표 있음)
                Pattern.compile("창구입금\\s?(\\d{1,3}(?:,\\d{3})*)(원)?"), // 패턴: 창구입금 (쉼표 있음)

                // 쉼표가 없는 형식
                Pattern.compile("입금\\s?(\\d+)(원)?"), // 패턴: 입금 (쉼표 없음)
                Pattern.compile("지급\\s?(\\d+)(원)?"), // 패턴: 지급 (쉼표 없음)
                Pattern.compile("전자금융입금\\s?(\\d+)(원)?"), // 패턴: 전자금융입금 (쉼표 없음)
                Pattern.compile("CMS입금\\s?(\\d+)(원)?"), // 패턴: CMS입금 (쉼표 없음)
                Pattern.compile("무통장입금\\s?(\\d+)(원)?"), // 패턴: 무통장입금 (쉼표 없음)
                Pattern.compile("창구입금\\s?(\\d+)(원)?") // 패턴: 창구입금 (쉼표 없음)
        };

        // 패턴 매칭
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                amount = matcher.group(1).replace(",", "").trim(); // 쉼표 제거 및 트림
                break;
            }
        }

        return amount;
    }

    private String extractDepositorFromBody(String body) {
        String depositor = "No depositor found";

        // 모든 줄 바꿈 문자를 \n으로 변환
        body = body.replace("\r\n", "\n");

        // 디버그 로그: 처리된 메시지 본문 출력
        System.out.println("Processed message body: " + body);

        // 패턴 목록
        Pattern[] patterns = {
                // 하나 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n하나,\\d{2}/\\d{2},\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n하나,\\d{1,10}/\\d{1,10},\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n하나,\\d{2}/\\d{2},\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n하나,\\d{1,10}/\\d{1,10},\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n하나,\\d{2}/\\d{2},\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n하나,\\d{1,10}/\\d{1,10},\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("하나,\\d{2}/\\d{2},\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("하나,\\d{1,10}/\\d{1,10},\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),

                // 새마을금고 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}", Pattern.DOTALL),
                Pattern.compile("<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}", Pattern.DOTALL),
                Pattern.compile("<새마을금고>\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*원\\n\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}", Pattern.DOTALL),

                // 케이뱅크 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),

                // KB 관련 패턴들
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n전자금융입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),

                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\nCMS입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),

                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(,?:\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(,?:\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n무통장입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),

                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[KB\\]\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n(\\S+)\\n창구입금\\s*\\d{1,3}(?:,\\d{3})*\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),

                // 우리 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("우리\\s*\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("우리\\s*\\d{1,10}/\\d{1,10}\\s*\\d{1,10}:\\d{1,10}\\n\\*+\\d{1,10}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),

                // 신한 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[신한은행\\]\\s*\\d{2}/\\d{2}\\n\\d{2}:\\d{2}:\\d{2}\\.\\d{2}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)", Pattern.DOTALL),
                Pattern.compile("\\[신한은행\\]\\s*\\d{1,10}/\\d{1,10}\\n\\d{1,10}:\\d{1,10}:\\d{1,10}\\.\\d{1,10}\\n\\[\\d{1,10}-\\*+-\\d{1,10}\\]\\n지급\\s*\\d{1,3}(?:,\\d{3})*원\\n\\(([^\\)]+)\\)\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),

                // 우체국 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n우체국,\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n우체국,\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n우체국,\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n우체국,\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n우체국,\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n우체국,\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("우체국,\\d{2}:\\d{2}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
                Pattern.compile("우체국,\\d{1,10}:\\d{1,10}\\n\\d{1,10}\\*+\\d{1,10}\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),

                // 케이뱅크 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),
                Pattern.compile("\\[케이뱅크\\]\\n(\\S+)\\n입금\\d{1,3}(?:,\\d{3})*원\\n잔액\\d{1,3}(?:,\\d{3})*", Pattern.DOTALL),

                // 농협 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n농협\\s*입금\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\s*\\d{1,3}-\\d{1,3}-\\*+\\d{1,6}\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n농협\\s*입금\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\s*\\d{1,3}-\\d{1,3}-\\*+\\d{1,6}\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n농협\\s*입금\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\s*\\d{1,3}-\\d{1,3}-\\*+\\d{1,6}\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),
                Pattern.compile("농협\\s*입금\\d{1,3}(?:,\\d{3})*원\\n\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\s*\\d{1,3}-\\d{1,3}-\\*+\\d{1,6}\\n(\\S+)\\n잔액\\d{1,3}(?:,\\d{3})*원", Pattern.DOTALL),

                // 기업 관련 패턴
                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\d{4}/\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n\\d{1,10}\\*+\\d{1,10}\\n기업", Pattern.DOTALL),
                Pattern.compile("\\[국제발신\\]\\n\\d{4}/\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n\\d{1,10}\\*+\\d{1,10}\\n기업", Pattern.DOTALL),
                Pattern.compile("\\[Web발신\\]\\n\\d{4}/\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n\\d{1,10}\\*+\\d{1,10}\\n기업", Pattern.DOTALL),
                Pattern.compile("\\d{4}/\\d{2}/\\d{2}\\s*\\d{2}:\\d{2}\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)\\n\\d{1,10}\\*+\\d{1,10}\\n기업", Pattern.DOTALL)
        };


        // 패턴 매칭
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                depositor = matcher.group(1).trim();
                System.out.println("Matched Depositor: " + depositor); // 추가 로그
                break; // 첫 번째 일치하는 패턴을 찾으면 종료
            } else {
                System.out.println("Pattern not matched: " + pattern.pattern()); // 추가 로그
                System.out.println("Message body: " + body); // 메시지 본문 출력
            }
        }

        return depositor;
    }

    private boolean isIncomeRelatedMessage(String body) {
        for (String keyword : INCOME_KEYWORDS) {
            if (body.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

//    // 테스트용 메서드
//    private String extractDepositorFromBody2(String body) {
//        String depositor = "No depositor found";
//
//        // 모든 줄 바꿈 문자를 \n으로 변환
//        body = body.replace("\r\n", "\n");
//
//        // 디버그 로그: 처리된 메시지 본문 출력
//        System.out.println("Processed message body: " + body);
//
//        // 패턴 매칭 테스트
//        String PatternStr = "\\[국제발신\\]\\n\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)";
//        Pattern vPattern = Pattern.compile(PatternStr, Pattern.DOTALL);
//
//        // 패턴 목록
//        Pattern[] patterns = {
//                Pattern.compile("\\[국제발신\\]\\n\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
//                Pattern.compile("\\[국제발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
//                Pattern.compile("\\[Web발신\\]\\n\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
//                Pattern.compile("\\[케이뱅크\\]\\n(\\S+)\\n입금\\s*\\d{1,3}(?:,\\d{3})*원\\n잔액\\s*\\d{1,3}(?:,\\d{3})*원\\n(\\S+)", Pattern.DOTALL),
//        };
//
//
//        // 패턴 매칭
//        for (Pattern pattern : patterns) {
//            Matcher matcher = pattern.matcher(body);
//            if (matcher.find()) {
//                depositor = matcher.group(1).trim();
//                System.out.println("Matched Depositor: " + depositor); // 추가 로그
//                break; // 첫 번째 일치하는 패턴을 찾으면 종료
//            } else {
//                System.out.println("Pattern not matched: " + pattern.pattern()); // 추가 로그
//                System.out.println("Message body: " + body); // 메시지 본문 출력
//            }
//        }
//
//        // 패턴과 실제 문자열 비교 (매치 실패시)
//        if (!vPattern.matcher(body).matches()) {
//            System.out.println("v pattern does not match body:");
//            String[] patternParts = PatternStr.split("\\\\n");
//            String[] bodyParts = body.split("\\n");
//            for (int i = 0; i < Math.min(patternParts.length, bodyParts.length); i++) {
//                System.out.println("Pattern part: " + patternParts[i]);
//                System.out.println("Body part: " + bodyParts[i]);
//                System.out.println("Match: " + bodyParts[i].matches(patternParts[i]));
//            }
//        }
//
//        // 패턴과 실제 문자열 비교 (매치 성공시)
//        if (vPattern.matcher(body).matches()) {
//            System.out.println("v pattern does match body:");
//            String[] patternParts = PatternStr.split("\\\\n");
//            String[] bodyParts = body.split("\\n");
//            for (int i = 0; i < Math.min(patternParts.length, bodyParts.length); i++) {
//                System.out.println("Pattern part: " + patternParts[i]);
//                System.out.println("Body part: " + bodyParts[i]);
//                System.out.println("Match: " + bodyParts[i].matches(patternParts[i]));
//            }
//        }
//
//        return depositor;
//    }
}
