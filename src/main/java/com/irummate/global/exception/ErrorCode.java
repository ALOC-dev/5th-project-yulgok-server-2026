package com.irummate.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    BIO_TOO_LONG(HttpStatus.BAD_REQUEST, "자기소개는 100자를 초과할 수 없습니다."),
    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "JPG/PNG 형식의 이미지만 업로드 가능합니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지는 5MB를 초과할 수 없습니다."),
    PAST_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "과거 날짜는 설정할 수 없습니다."),
    EMPTY_CHAT_MESSAGE(HttpStatus.BAD_REQUEST, "채팅 메시지 내용이 비어있습니다."),
    CHAT_MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "채팅 메시지는 500자를 초과할 수 없습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 토큰이 없거나 만료되었습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    WEBSOCKET_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "웹소켓 인증에 실패했습니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ADMIN_ONLY(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다."),
    CERTIFICATION_REQUIRED(HttpStatus.FORBIDDEN, "기숙사 인증이 필요합니다."),
    SURVEY_REQUIRED(HttpStatus.FORBIDDEN, "설문 제출이 필요합니다."),
    NOT_CHAT_PARTICIPANT(HttpStatus.FORBIDDEN, "해당 채팅방의 참여자가 아닙니다."),
    NOT_MATCH_PARTICIPANT(HttpStatus.FORBIDDEN, "해당 매칭의 당사자가 아닙니다."),
    CHAT_ROOM_CLOSED(HttpStatus.FORBIDDEN, "상대방이 나가서 메시지를 보낼 수 없는 채팅방입니다."),
    MATCH_NOT_OPEN(HttpStatus.FORBIDDEN, "오늘은 매칭 가능한 날짜가 아닙니다."),
    ADMIN_CERTIFICATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "관리자는 일반 사용자용 기숙사 인증 요청을 생성할 수 없습니다."),
    USER_DETAILS_REQUIRED(HttpStatus.FORBIDDEN, "필수 정보를 입력한 후 인증 요청을 생성할 수 있습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "인증 요청을 찾을 수 없습니다."),
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "설문 정보를 찾을 수 없습니다."),
    MATCH_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "매칭 요청을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    MATCH_CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "추천 가능한 매칭 상대가 없습니다."),
    MATCH_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "매칭 날짜가 설정되어 있지 않습니다."),

    // 409
    DETAILS_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 필수 정보가 등록된 상태입니다."),
    CERTIFICATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 인증 요청이 존재합니다."),
    CERTIFICATION_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 인증 요청입니다."),
    SURVEY_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "이미 설문을 제출한 상태입니다."),
    HEART_ALREADY_SENT(HttpStatus.CONFLICT, "이미 해당 사용자에게 하트를 보냈습니다."),
    ALREADY_CONFIRMED(HttpStatus.CONFLICT, "이미 최종 확정된 상태입니다."),
    MATCH_ALREADY_REROLLED_TODAY(HttpStatus.CONFLICT, "오늘은 이미 매칭 추천을 완료했습니다."),
    NOT_REJECTABLE_STATUS(HttpStatus.CONFLICT, "거절할 수 없는 상태입니다."),
    NOT_HEARTABLE_STATUS(HttpStatus.CONFLICT, "하트를 보낼 수 없는 상태입니다."),
    NOT_CONFIRMABLE_STATUS(HttpStatus.CONFLICT, "매칭을 확정할 수 없는 상태입니다."),

    // 422
    NOT_ACCEPTED_STATUS(HttpStatus.UNPROCESSABLE_ENTITY, "ACCEPTED 상태에서만 확정할 수 있습니다."),

    // 500
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 통신 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
