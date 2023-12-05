package com.chingchan.theClap.globalData

//THE_CLAP custom error
enum class ErrorCode(val message: String) {
    S00001("Success"),

    //Login
    S00002("THE CLAP에 가입된 정보가 없습니다."),
    S00003("로그인 타입이 맞지 않습니다."),
    S00004("소셜 로그인 실패"),
    S00005("회원가입 닉네임이 중복입니다."),

    //User
    S00011("해당 USER가 없습니다."),
    S00012("FOLLOW 대상 USER가 없습니다."),
    S00013("해당 FOLLOW가 없습니다."),
    S00014("BLOCK 대상 USER가 없습니다."),
    S00015("해당 BLOCK이 없습니다."),
    S00016("REPORT 대상 USER가 없습니다."),

    //Board
    S00017("해당 카테고리가 존재합니다."),
    S00018("카테고리를 찾을 수 없습니다."),
    S00019("해당 게시글을 찾을 수 없습니다."),
    S00020("제목을 입력하세요."),
    S00021("제목은 30자 이상 입력할 수 없습니다. "),
    S00022("본인이 작성한 댓글만 수정 가능 합니다."),
    S00023("본인이 작성한 댓글만 삭제 가능 합니다."),
}