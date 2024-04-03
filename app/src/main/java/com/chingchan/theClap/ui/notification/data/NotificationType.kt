package com.chingchan.theClap.ui.notification.data

enum class NotificationType(val code: Int) {
    TYPE01(1),  // 팔로우 관련
    TYPE02(2),  // 댓글 관련
    TYPE03(3),   // 게시글 좋아요 관련
    TYPE04(4),   // 게시글 삭제, 신고 관련
    TYPE05(5),   // 인기글 등록 관련
    TYPE06(6)   // 유저 신고 관련
}