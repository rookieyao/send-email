package com.rookie.send.email.param;

public enum BodyType {

    EMAIL_TEXT_BODY("email_text_key","内部审核：您%s年度卫生资格考试未通过/内部复核查询成绩/修改成绩/百分百一次合格/加内部老师Q+%s/包拿证/"),
//    EMAIL_TEXT_BODY("email_text_key","你好，恭喜您通过我司%s年的面试，邀请你入职我们公司，如有疑问，请联系qq:%s"),
    EMAIL_IMAGE_BODY("email_image_key","图片名称：%s"),
    EMAIL_FILE_BODY("email_file_key","文件名称：%s");

    private String code ;
    private String value ;
    BodyType (String code, String value){
        this.code = code ;
        this.value = value ;
    }

    public static String getByCode (String code){
        BodyType[] values = BodyType.values() ;
        for (BodyType bodyType : values) {
            if (bodyType.code.equalsIgnoreCase(code)){
                return bodyType.value ;
            }
        }
        return null ;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
